package pl.joegreen.sergeants.framework.model;

import pl.joegreen.sergeants.framework.model.api.UpdatableGameState;

public interface GameStarted {
    /**
     * See {@link UpdatableGameState#getReplayId()}
     */
    String getReplayId();

    String[] getUsernames();

    int getPlayerIndex();

    String getChatRoom();

    String getTeamChatRoom();
}
