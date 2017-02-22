package pl.joegreen.sergeants.api.response;

import lombok.Value;

@Value
public class ChatMessageApiResponse {
    /**
     * Name of the author of the message.
     */
    private String username;
    /**
     * Player index of the author of the message.
     */
    private int playerIndex;
    /**
     * Actual content of the message
     */
    private String text;
}
