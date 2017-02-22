package pl.joegreen.sergeants.framework;

import pl.joegreen.sergeants.framework.model.ChatMessage;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.GameStarted;
import pl.joegreen.sergeants.framework.model.GameState;

import static org.slf4j.LoggerFactory.getLogger;

public interface Bot {
    default void onGameStateUpdate(GameState newGameState) {
        getLogger(Bot.class).info("Received new game state: " + newGameState.toString());
    }

    default void onGameFinished(GameResult gameResult) {
        getLogger(Bot.class).info("Game finished, result: " + gameResult);
    }

    default void onGameStarted(GameStarted gameStarted) {
        getLogger(Bot.class).info("Game started: " + gameStarted);
    }

    default void onChatMessage(ChatMessage chatMessage){
        getLogger(Bot.class).info("Chat message: " + chatMessage);
    }

}
