package pl.joegreen.sergeants.simulator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.api.GameStartedApiResponseImpl;
import pl.joegreen.sergeants.simulator.viewer.FileViewerWriter;
import pl.joegreen.sergeants.simulator.viewer.NopViewerWriter;
import pl.joegreen.sergeants.simulator.viewer.ViewerWriter;

import java.util.*;


/**
 * An offline local server simulator of one generals.io game.
 * Single threaded, so all action calls must be made from the same thread.
 * Use SimulatorFactory to create an instance.
 */
@Slf4j
public class Simulator {

    private final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private final GameMap gameMap;
    private final Player players[];
    private final List<SimulatorListener> listeners = new ArrayList<>();
    private final SimulatorConfiguration configuration;

    Simulator(GameMap gameMap, Player[] players, SimulatorConfiguration configuration) {
        if (gameMap.getGenerals().length != players.length) {
            throw new IllegalArgumentException("Game map generals and players has different length");
        }
        this.gameMap = gameMap;
        this.players = players;
        this.configuration = configuration;
    }

    /**
     * Start simulation
     *
     * @return the winner player index
     */
    public Optional<Integer> start() {
        LOGGER.info("Starting match with: {}", Arrays.toString(players));
        try(ViewerWriter viewerWriter = createViewerWriter()) {
            listeners.forEach(simulatorListener -> simulatorListener.beforeGameStart(players, gameMap));
            Arrays.stream(players).forEach(p -> {
                GameStartApiResponse startData = getStartData(p.getPlayerIndex());
                p.getBot().onGameStarted(new GameStartedApiResponseImpl(startData));
            });

            for (; ; ) {
                Map<Integer, PlayerStats> playersStats = calculatePlayersStats(gameMap.getTiles());
                Arrays.stream(players)
                        .map(player -> sendGameUpdate(player, playersStats))
                        .map(gameMap::move)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(this::endPlayer);
                gameMap.tick();
                viewerWriter.write(gameMap);
                listeners.forEach(simulatorListener -> simulatorListener.afterHalfTurn(gameMap.getHalfTurnCounter(), gameMap.getTiles()));

                boolean reachedMaxTurns = gameMap.getHalfTurnCounter() > (configuration.getMaxTurns() * 2);
                Player[] alive = Arrays.stream(this.players).filter(Player::isAlive).toArray(Player[]::new);
                if (alive.length == 1) {
                    Player winner = alive[0];
                    sendGameUpdate(winner, calculatePlayersStats(gameMap.getTiles()));
                    return endGame(winner);
                } else if (reachedMaxTurns) {
                    return disconnectPlayers(this.players);
                }
            }
        }
    }

    private ViewerWriter createViewerWriter() {
        return configuration.getReplayFile()
                .map(file -> (ViewerWriter) new FileViewerWriter(file))
                .orElseGet(NopViewerWriter::new);
    }

    private Player sendGameUpdate(Player player, Map<Integer, PlayerStats> playersStats) {
        player.updateGameState(getGameStateForPlayer(player.getPlayerIndex(), players, gameMap, playersStats));
        return player;
    }


    private void endPlayer(PlayerKilled playerKilled) {
        Player player = players[playerKilled.getVictim()];
        player.getMoves().clear();
        player.setDead(true);
        Optional<String> killer = Optional.of(player.getName());
        GameResult gameResult = new GameResult(GameResult.Result.LOST, player.getGameState(), killer);
        player.getBot().onGameFinished(gameResult);
    }

    private GameStartApiResponse getStartData(int playerIndex) {
        String[] participants = Arrays.stream(players)
                .map(Player::getName)
                .toArray(String[]::new);
        return new GameStartApiResponse(playerIndex, "replayId", "chatRoom", "teamChatRoom", participants, null);
    }

    private Optional<Integer> disconnectPlayers(Player[] players) {
        listeners.forEach(simulatorListener -> simulatorListener.onGameAborted(players));
        Arrays.stream(players).forEach(p -> {
            GameResult.Result result = GameResult.Result.DISCONNECTED;
            GameResult gameResult = new GameResult(result, p.getGameState(), Optional.empty());
            p.getBot().onGameFinished(gameResult);
        });
        return Optional.empty();
    }

    private Optional<Integer> endGame(Player winner) {
        listeners.forEach(simulatorListener -> simulatorListener.onGameEnd(winner));
        GameResult.Result result = GameResult.Result.WON;
        GameResult gameResult = new GameResult(result, winner.getGameState(), Optional.empty());
        winner.getBot().onGameFinished(gameResult);
        return Optional.of(winner.getPlayerIndex());
    }

    public List<SimulatorListener> getListeners() {
        return listeners;
    }

    GameState getGameStateForPlayer(int playerIndex, Player[] players, GameMap gameMap, Map<Integer, PlayerStats> playersStats) {
        return new SimulatorPlayerGameState(
                gameMap.getWidth(), gameMap.getHeight(), gameMap.getHalfTurnCounter(), players, playerIndex, getTilesVisibilitiesForPlayer(playerIndex, gameMap), playersStats
        );
    }

    private IdentityHashMap<Tile, Boolean> getTilesVisibilitiesForPlayer(int playerIndex, GameMap gameMap) {
        IdentityHashMap<Tile, Boolean> result = new IdentityHashMap<>();
        Arrays.stream(gameMap.getTiles()).forEach(tile -> result.put(tile, gameMap.isVisible(playerIndex, tile)));
        return result;
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    static class PlayerStats {
        public int army;
        public int tiles;
    }

    private Map<Integer, PlayerStats> calculatePlayersStats(Tile[] tiles) {
        Map<Integer, PlayerStats> result = new HashMap<>();
        Arrays.stream(tiles).forEach(tile -> {
            tile.getOwnerPlayerIndex().ifPresent(owner -> {
                        PlayerStats playerStats = result.getOrDefault(owner, new PlayerStats(0, 0));
                        playerStats.army += tile.getArmySize();
                        playerStats.tiles++;
                        result.put(owner, playerStats);
                    }
            );
        });
        return result;
    }

}
