package pl.joegreen.sergeants.framework;

import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.framework.model.GameState;

public interface Actions {
    void move(int indexFrom, int indexTo);

    void move(GameState.Field fieldFrom, GameState.Field fieldTo);

    void move(int indexFrom, int indexTo, boolean moveHalf);

    void move(GameState.Field fieldFrom, GameState.Field fieldTo, boolean moveHalf);

    void sendChat(String message);

    void sendTeamChat(String message);

    void leaveGame();

    void ping(int index);

    void ping(GameState.Field field);

    void clearMoves();

    GeneralsApi getBareApi();
}
