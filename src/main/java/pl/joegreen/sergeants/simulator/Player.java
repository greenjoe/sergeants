package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.api.UpdatableGameState;

import java.util.Deque;

class Player {
    private final String name;
    private final int playerIndex;
    private final Bot bot;
    private final Deque<Move> moves;
    private UpdatableGameState gameState;
    private boolean dead = false;


    public Player(int playerIndex, Bot bot, Deque<Move> moves) {
        this.playerIndex = playerIndex;
        this.bot = bot;
        this.moves = moves;
        this.name = bot.toString();
    }

    public Bot getBot() {
        return bot;
    }

    public Deque<Move> getMoves() {
        return moves;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setInitialGameState(UpdatableGameState gameState) {
        this.gameState = gameState;
        bot.onGameStateUpdate(gameState);
    }

    public void updateGameState(GameUpdateApiResponse gameUpdateApiResponses) {
        gameState = gameState.update(gameUpdateApiResponses);
        bot.onGameStateUpdate(gameState);
    }

    public String getName() {
        return name;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isAlive() {
        return !dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
