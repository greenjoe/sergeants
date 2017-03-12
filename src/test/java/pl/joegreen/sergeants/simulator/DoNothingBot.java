package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.model.GameState;

class DoNothingBot extends TestBot {
    private final Actions actions;

    public DoNothingBot(Actions actions) {
        this.actions = actions;
    }

    @Override
    public void onGameStateUpdate(GameState newGameState) {
        super.onGameStateUpdate(newGameState);
        actions.sendChat("I do nothing");
    }
}
