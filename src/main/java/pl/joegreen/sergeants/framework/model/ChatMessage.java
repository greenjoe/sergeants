package pl.joegreen.sergeants.framework.model;

import lombok.Value;

@Value
public class ChatMessage {
    public enum ChatType {
        GAME, TEAM, UNKNOWN
    }
    ChatType roomType;
    String message;
    String username;
}
