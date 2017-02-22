package pl.joegreen.sergeants.framework.model;

import lombok.EqualsAndHashCode;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;

@EqualsAndHashCode
public class GameStarted {

    private final GameStartApiResponse gameStartApiResponse;

    public GameStarted(GameStartApiResponse gameStartApiResponse) {
        this.gameStartApiResponse = gameStartApiResponse;
    }

    /**
     * See {@link GameState#getReplayId()}
     */
    public String getReplayId() {
        return gameStartApiResponse.getReplayId();
    }

    public String[] getUsernames() {
        return gameStartApiResponse.getUsernames();
    }
}
