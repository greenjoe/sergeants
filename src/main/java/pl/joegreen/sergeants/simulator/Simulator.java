package pl.joegreen.sergeants.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.api.GameStartedApiResponseImpl;
import pl.joegreen.sergeants.framework.model.api.UpdatableGameState;

import java.util.Arrays;
import java.util.Optional;


/**
 * An offline local server simulator of one generals.io game.
 * Single threaded, so all action calls must be made from the same thread.
 * Use SimulatorFactory to create an instance.
 */
public class Simulator {

    private final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);
    private final int maxTurns;
    private final Player players[];
    private final GameMap gameMap;

    Simulator(GameMap gameMap, int maxTurns, Player[] players) {
        this.gameMap = gameMap;
        this.maxTurns = maxTurns;
        this.players = players;
    }

    /**
     * Start simulation
     *
     * @return the winner
     */
    public Optional<Player> start() {
        LOGGER.info("Starting match with: {}", Arrays.toString(players));
        Arrays.stream(players).forEach(p -> {
            GameStartApiResponse startData = getStartData(p.getPlayerIndex());
            p.getBot().onGameStarted(new GameStartedApiResponseImpl(startData));
        });

        Arrays.stream(players).forEach(this::sendInitialGameUpdate);

        for (; ; ) {
            Arrays.stream(players)
                    .map(this::sendGameUpdate)
                    .map(Player::getMoves)
                    .map(gameMap::move)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(this::endPlayer);
            gameMap.tick();

            boolean reachedMaxTurns = gameMap.getHalfTurnCounter() > (maxTurns * 2);
            Player[] alive = Arrays.stream(this.players).filter(Player::isAlive).toArray(Player[]::new);
            if (alive.length == 1) {
                return endGame(alive[0]);
            } else if (reachedMaxTurns) {
                return disconnectPlayers(this.players);
            }
        }
    }

    private Player sendGameUpdate(Player player) {
        GameUpdateApiResponse update = gameMap.getUpdate(player.getPlayerIndex());
        player.updateGameState(update);
        return player;
    }

    private void sendInitialGameUpdate(Player player) {
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

    private Optional<Player> disconnectPlayers(Player[] players) {
        gameMap.saveGameAsJson();
        Arrays.stream(players).forEach(p -> {
            GameResult.Result result = GameResult.Result.DISCONNECTED;
            GameResult gameResult = new GameResult(result, p.getGameState(), Optional.empty());
            p.getBot().onGameFinished(gameResult);
        });
        return Optional.empty();
    }

    private Optional<Player> endGame(Player winner) {
        gameMap.saveGameAsJson();
        GameResult.Result result = GameResult.Result.WON;
        GameResult gameResult = new GameResult(result, winner.getGameState(), Optional.empty());
        winner.getBot().onGameFinished(gameResult);
        return Optional.of(winner);
    }

}
