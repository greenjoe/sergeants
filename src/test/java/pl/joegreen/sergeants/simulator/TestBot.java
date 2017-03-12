package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.GameState;

import java.util.ArrayList;
import java.util.List;

public abstract class TestBot implements Bot {
    public final List<GameState> receivedGameStates = new ArrayList<>();

    public void onGameStateUpdate(GameState newGameState) {
        receivedGameStates.add(newGameState);
    }
}
