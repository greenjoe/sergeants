package pl.joegreen.sergeants.framework;

import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.api.listener.NoArgsListener;
import pl.joegreen.sergeants.api.response.ChatMessageApiResponse;
import pl.joegreen.sergeants.api.response.GameLostApiResponse;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.framework.model.ChatMessage;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.GameStarted;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.queue.QueueConfiguration;
import pl.joegreen.sergeants.framework.user.UserConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Games {
    private final static Logger LOGGER = LoggerFactory.getLogger(Games.class);
    private static final String HELLO_MESSAGE = "Bot created with the Sergeants framework: https://git.io/sergeants";
    private GeneralsApi api = null;
    private Function<Actions, Bot> botProvider;
    private QueueConfiguration queueConfiguration;
    private GameState currentGameState;
    private GameStartApiResponse gameStartApiResponse;
    private Bot bot;
    private Actions actions;
    private int gamesToPlay = 0;
    private UserConfiguration userConfiguration;
    private List<GameResult> gameResults = new ArrayList<>();
    private CompletableFuture<List<GameResult>> gameResultsFuture;

    private boolean inGame;


    public static CompletableFuture<List<GameResult>> playAsynchronously(int games, Function<Actions, Bot> botProvider, QueueConfiguration queueConfiguration, UserConfiguration userConfiguration) {
        if (games <= 0) {
            throw new IllegalArgumentException("Number of games must be positive");
        }
        CompletableFuture<List<GameResult>> resultsFuture = new CompletableFuture<>();
        new Games(GeneralsApi.create(), games, botProvider, queueConfiguration, userConfiguration, resultsFuture).playRound();
        return resultsFuture;
    }

    public static CompletableFuture<List<GameResult>> playAsynchronouslyWithCustomApi(GeneralsApi api, int games, Function<Actions, Bot> botProvider, QueueConfiguration queueConfiguration, UserConfiguration userConfiguration) {
        if (games <= 0) {
            throw new IllegalArgumentException("Number of games must be positive");
        }
        CompletableFuture<List<GameResult>> resultsFuture = new CompletableFuture<>();
        new Games(api, games, botProvider, queueConfiguration, userConfiguration, resultsFuture).playRound();
        return resultsFuture;
    }


    public static List<GameResult> play(int games, Function<Actions, Bot> botProvider, QueueConfiguration queueConfiguration, UserConfiguration userConfiguration) {
        try {
            return playAsynchronously(games, botProvider, queueConfiguration, userConfiguration).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Games(GeneralsApi api, int games, Function<Actions, Bot> botProvider, QueueConfiguration queueConfiguration,
                  UserConfiguration userConfiguration, CompletableFuture<List<GameResult>> resultsFuture) {
        this.gamesToPlay = games;
        this.botProvider = botProvider;
        this.userConfiguration = userConfiguration;
        this.queueConfiguration = queueConfiguration;
        this.api = configureApi(api);
        this.actions = new BotActions();
        this.gameResultsFuture = resultsFuture;
    }

    private GeneralsApi configureApi(GeneralsApi api) {
        return api
                .onDisconnected(this::onDisconnected)
                .onGameStarted(this::gameStarted)
                .onGameUpdated(this::onGameUpdated)
                .onGameLost(this::onGameLost)
                .onGameWon(this::onGameWon)
                .onSetUsernameError(LOGGER::error)
                .onChatMessage(this::onChatMessage);
    }

    private void onChatMessage(String room, ChatMessageApiResponse chatMessageApiResponse) {
        if (bot != null) {
            runBotMethodCatchingExceptions(
                    () -> bot.onChatMessage(new ChatMessage(
                            determineRoomType(room, gameStartApiResponse),
                            chatMessageApiResponse.getText(),
                            chatMessageApiResponse.getUsername()
                    ))
            );
        }
    }

    private ChatMessage.ChatType determineRoomType(String room, GameStartApiResponse gameStartApiResponse) {
        if (room.equals(gameStartApiResponse.getChatRoom())) {
            return ChatMessage.ChatType.GAME;
        } else if (room.equals(gameStartApiResponse.getTeamChatRoom())) {
            return ChatMessage.ChatType.TEAM;
        } else {
            return ChatMessage.ChatType.UNKNOWN;
        }
    }

    private void onGameFinished(GameResult gameResult) {
        if (inGame) {
            inGame = false;
            gamesToPlay--;
            LOGGER.debug("Game finished, {} games left, result: ", gamesToPlay, gameResult);
            runBotMethodCatchingExceptions(() -> bot.onGameFinished(gameResult));
            gameResults.add(gameResult);
            if (gamesToPlay > 0) {
                playRound();
            } else {
                LOGGER.debug("All games finished, disconnecting from the API");
                gameResultsFuture.complete(gameResults);
                api.disconnect();
            }
        }
    }

    private void onDisconnected() {
        GameResult gameResult = new GameResult(GameResult.Result.DISCONNECTED, currentGameState, Optional.empty());
        onGameFinished(gameResult);
    }

    private void onGameWon() {
        GameResult gameResult = new GameResult(GameResult.Result.WON, currentGameState, Optional.empty());
        onGameFinished(gameResult);
    }

    private void onGameLost(GameLostApiResponse gameLostApiResponse) {
        GameResult gameResult = new GameResult(GameResult.Result.LOST, currentGameState,
                Optional.of(tryToGetKillerName(gameLostApiResponse)));
        onGameFinished(gameResult);
    }

    private String tryToGetKillerName(GameLostApiResponse gameLostApiResponse) {
        try {
            return currentGameState.getPlayers().get(gameLostApiResponse.getKiller()).getUsername();
        } catch (Exception ex) {
            LOGGER.error("Cannot find out the killer name on game lost", ex);
            return "UNKNOWN";
        }
    }


    private void connectIfNeededAndThen(NoArgsListener listener) {
        if (!api.isConnected()) {
            api.onceConnected(() -> {
                LOGGER.debug("Connected to the API");
                userConfiguration.configureUsername(api);
                listener.onEvent();
            });
            api.connect();
        } else {
            listener.onEvent();
        }
    }

    private void playRound() {
        connectIfNeededAndThen(() -> {
            LOGGER.info("Joining game queue with configuration" + queueConfiguration);
            queueConfiguration.joinQueue(api, userConfiguration.getUserId());});
    }


    private void onGameUpdated(GameUpdateApiResponse gameUpdateApiResponse) {
        LOGGER.trace("Game update: {}", gameUpdateApiResponse);
        if (currentGameState == null) {
            currentGameState = GameState.createInitialGameState(gameStartApiResponse, gameUpdateApiResponse);
        } else {
            currentGameState = currentGameState.update(gameUpdateApiResponse);
        }
        runBotMethodCatchingExceptions(() -> bot.onGameStateUpdate(currentGameState));
    }

    private void gameStarted(GameStartApiResponse gameStartApiResponse) {
        inGame = true;
        LOGGER.debug("Game started: {}", gameStartApiResponse);
        this.gameStartApiResponse = gameStartApiResponse;
        bot = botProvider.apply(actions);
        runBotMethodCatchingExceptions(() -> bot.onGameStarted(new GameStarted(gameStartApiResponse)));
        actions.sendChat(HELLO_MESSAGE);
        currentGameState = null;
    }

    private void runBotMethodCatchingExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception ex) {
            LOGGER.error("Exception while calling bot method. The game will be continued, but bot state may become inconsistent.", ex);
        }
    }


    private class BotActions implements Actions {
        private final Logger LOGGER = LoggerFactory.getLogger(BotActions.class);

        @Override
        public void move(int indexFrom, int indexTo) {
            move(indexFrom, indexTo, false);
        }

        @Override
        public void move(GameState.Field fieldFrom, GameState.Field fieldTo) {
            move(fieldFrom.getIndex(), fieldTo.getIndex(), false);
        }

        @Override
        public void move(GameState.Field fieldFrom, GameState.Field fieldTo, boolean moveHalf) {
            if (!fieldFrom.getNeighbours().contains(fieldTo)) {
                LOGGER.error("Moving between fields that are not neighbours, it probably has no effect. {} ==> {}", fieldFrom, fieldTo);
            }
            move(fieldFrom.getIndex(), fieldTo.getIndex(), moveHalf);
        }

        @Override
        public void move(int indexFrom, int indexTo, boolean moveHalf) {
            LOGGER.trace("Move from {} to {}, half={}", indexFrom, indexTo, moveHalf);
            api.attack(indexFrom, indexTo, moveHalf);
        }

        @Override
        public void sendChat(String message) {
            LOGGER.debug("Sending chat message: " + message);
            api.sendChatMessage(gameStartApiResponse.getChatRoom(), message);
        }

        @Override
        public void sendTeamChat(String message) {
            if (currentGameState.getTeamChatRoom() != null) {
                LOGGER.debug("Sending team chat message:" + message);
                api.sendChatMessage(gameStartApiResponse.getTeamChatRoom(), message);
            } else {
                LOGGER.error("Cannot send team chat message, there's no team chat");
            }
        }

        @Override
        public void leaveGame() {
            api.leaveGame();
        }

        @Override
        public void ping(int index) {
            LOGGER.trace("Pinging {}", index);
            api.ping(index);
        }

        @Override
        public void ping(GameState.Field field) {
            ping(field.getIndex());
        }

        @Override
        public void clearMoves() {
            LOGGER.debug("Clearing moves queue");
            api.clearMoves();
        }

        @Override
        public GeneralsApi getBareApi() {
            return api;
        }


    }

}
