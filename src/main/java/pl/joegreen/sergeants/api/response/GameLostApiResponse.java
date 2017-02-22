package pl.joegreen.sergeants.api.response;

import lombok.Value;

@Value
public class GameLostApiResponse {
    /**
     * Index of the player that killed the bot.
     */
    private int killer;
}
