package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.GameState;

import java.util.function.Function;

class DoSameMoveBot extends TestBot {
    private final Actions actions;
    private final int indexFrom;
    private final int indexTo;

    public DoSameMoveBot(Actions actions, int indexFrom, int indexTo) {
        this.actions = actions;
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
    }

    @Override
    public void onGameStateUpdate(GameState newGameState) {
        super.onGameStateUpdate(newGameState);
        actions.move(indexFrom, indexTo);
    }

    public static Function<Actions, Bot> forMove(int indexFrom, int indexTo){
        return actions -> new DoSameMoveBot(actions, indexFrom, indexTo);
    }
}
