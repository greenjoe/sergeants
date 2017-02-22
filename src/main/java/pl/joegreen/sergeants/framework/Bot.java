package pl.joegreen.sergeants.framework;

import pl.joegreen.sergeants.framework.model.ChatMessage;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.GameStarted;
import pl.joegreen.sergeants.framework.model.GameState;

import static org.slf4j.LoggerFactory.getLogger;

public interface Bot {

    /**
     * Called on every game state update from server.
     * @param newGameState Contains current state of the game.
     */
    default void onGameStateUpdate(GameState newGameState) {
        getLogger(Bot.class).info("Received new game state: " + newGameState.toString());
    }

    /**
     * Called when game is won, lost or the connection to the server is lost.
     * @param gameResult Result of the game.
     */
    default void onGameFinished(GameResult gameResult) {
        getLogger(Bot.class).info("Game finished, result: " + gameResult);
    }

    /**
     * Called when the new game is started.
     * @param gameStarted Contains basic info about the game (replay id, players in the game).
     */
    default void onGameStarted(GameStarted gameStarted) {
        getLogger(Bot.class).info("Game started: " + gameStarted);
    }

    /**
     * Called on every chat message (game chat, team chat)
     */
    default void onChatMessage(ChatMessage chatMessage){
        getLogger(Bot.class).info("Chat message: " + chatMessage);
    }

}
