package pl.joegreen.sergeants.framework.model.api;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.framework.model.GameStarted;

@EqualsAndHashCode
@ToString
public class GameStartedApiResponseImpl implements GameStarted {

    private final GameStartApiResponse gameStartApiResponse;

    public GameStartedApiResponseImpl(GameStartApiResponse gameStartApiResponse) {
        this.gameStartApiResponse = gameStartApiResponse;
    }

    @Override
    public String getReplayId() {
        return gameStartApiResponse.getReplayId();
    }

    @Override
    public String[] getUsernames() {
        return gameStartApiResponse.getUsernames();
    }

    @Override
    public int getPlayerIndex(){
        return gameStartApiResponse.getPlayerIndex();
    }

    @Override
    public String getChatRoom(){
        return gameStartApiResponse.getChatRoom();
    }

    @Override
    public String getTeamChatRoom(){
        return gameStartApiResponse.getTeamChatRoom();
    }
}
