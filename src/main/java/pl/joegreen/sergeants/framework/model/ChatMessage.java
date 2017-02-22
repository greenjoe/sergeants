package pl.joegreen.sergeants.framework.model;

import lombok.Value;

@Value
public class ChatMessage {
    public enum ChatType {
        GAME, TEAM, UNKNOWN
    }
    ChatType roomType;
    String message;
    /**
     * Name of player sending the message. Can be null for server messages.
     */
    String username;
}
