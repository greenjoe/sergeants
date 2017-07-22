package pl.joegreen.sergeants.simulator;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.api.GameStartedApiResponseImpl;
import pl.joegreen.sergeants.framework.model.api.UpdatableGameState;
import pl.joegreen.sergeants.simulator.viewer.FileViewerWriter;
import pl.joegreen.sergeants.simulator.viewer.NopViewerWriter;
import pl.joegreen.sergeants.simulator.viewer.ViewerWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


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

            Arrays.stream(players).forEach(this::setInitialGameUpdate);

            for (; ; ) {
                Arrays.stream(players)
                        .map(this::sendGameUpdate)
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
                    sendGameUpdate(winner);
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

    private Player sendGameUpdate(Player player) {
        GameUpdateApiResponse update = gameMap.getUpdate(player.getPlayerIndex());
        player.updateGameState(update);
        return player;
    }

    private void setInitialGameUpdate(Player player) {
        int playerIndex = player.getPlayerIndex();
        GameStartApiResponse startData = getStartData(playerIndex);
        GameUpdateApiResponse gameUpdateApiResponses = gameMap.getUpdate(playerIndex);
        player.setInitialGameState(UpdatableGameState.createInitialGameState(startData, gameUpdateApiResponses));
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
}
