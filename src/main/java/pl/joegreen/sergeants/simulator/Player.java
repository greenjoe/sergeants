package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.GameState;

import java.util.Deque;

class Player {
    private final String name;
    private final int playerIndex;
    private final Bot bot;
    private final Deque<Move> moves;
    private GameState gameState;
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

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
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
