package pl.joegreen.sergeants.framework;

import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.framework.model.Field;

public interface Actions {

    void move(int indexFrom, int indexTo);

    /**
     * Direct move between fields. Works only if both fields are neighbours (either horizontally or vertically, not diagonally) - otherwise does nothing and logs error.
     */
    void move(Field fieldFrom, Field fieldTo);

    void move(int indexFrom, int indexTo, boolean moveHalf);

    /**
     * Direct move between fields. Works only if both fields are neighbours (either horizontally or vertically, not diagonally) - otherwise does nothing and logs error.
     * @param moveHalf if true, only half of the army will be moved (equal to double click in browser)
     */
    void move(Field fieldFrom, Field fieldTo, boolean moveHalf);

    void sendChat(String message);

    void sendTeamChat(String message);

    void leaveGame();

    void ping(int index);

    void ping(Field field);

    /**
     * Remove all moves from the move queue.
     */
    void clearMoves();

    /**
     * Get lower level API that potentially can make it possible to do something that is not possible by this interface.
     * Use with caution, as using it can potentially make the framework work unexpectedly.
     */
    GeneralsApi getBareApi();
}
