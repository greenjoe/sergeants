package pl.joegreen.sergeants.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.model.Field;

import java.util.Deque;

class SimulatorActions implements Actions {
    private final Logger LOGGER = LoggerFactory.getLogger(SimulatorActions.class);
    private final Deque<Move> moves;

    SimulatorActions(Deque<Move> moves) {
        this.moves = moves;
    }

    @Override
    public void move(int indexFrom, int indexTo) {
        move(indexFrom, indexTo, false);
    }

    @Override
    public void move(Field fieldFrom, Field fieldTo) {
        move(fieldFrom.getIndex(), fieldTo.getIndex(), false);
    }

    @Override
    public void move(Field fieldFrom, Field fieldTo, boolean moveHalf) {
        if (!fieldTo.getPosition().isMovableFrom(fieldFrom.getPosition())) {
            LOGGER.error("Moving between fields that are not neighbours, it probably has no effect. {} ==> {}", fieldFrom, fieldTo);
        }
        move(fieldFrom.getIndex(), fieldTo.getIndex(), moveHalf);
    }

    @Override
    public void move(int indexFrom, int indexTo, boolean moveHalf) {
        LOGGER.trace("Move from {} to {}, half={}", indexFrom, indexTo, moveHalf);
        moves.add(new Move(indexFrom, indexTo, moveHalf));
    }

    @Override
    public void sendChat(String message) {
        LOGGER.debug("Sending chat message: " + message);
    }

    @Override
    public void sendTeamChat(String message) {
        LOGGER.debug("Sending team chat message:" + message);
    }

    @Override
    public void leaveGame() {
        LOGGER.debug("LeaveGame");
    }

    @Override
    public void ping(int index) {
        LOGGER.debug("Pinging {}", index);
    }

    @Override
    public void ping(Field field) {
        ping(field.getIndex());
    }

    @Override
    public void clearMoves() {
        LOGGER.debug("Clearing moves queue");
        moves.clear();
    }

    @Override
    public GeneralsApi getBareApi() {
        return null;
    }

}
