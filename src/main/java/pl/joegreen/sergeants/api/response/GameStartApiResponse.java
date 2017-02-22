package pl.joegreen.sergeants.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import pl.joegreen.sergeants.api.GeneralsApi;

@Value
public class GameStartApiResponse {
    /**
     * Index of bot's player.
     */
    private int playerIndex;
    /**
     * Replay identifier. Use http://bot.generals.io/replays/{replayId} to see the replay in your browser.
     */
    @JsonProperty("replay_id")
    private String replayId;
    /**
     * Identifier of the main chat room, useful when sending a chat message ({@link GeneralsApi#sendChatMessage(String, String)}).
     */
    @JsonProperty("chat_room")
    private String chatRoom;
    /**
     * Identifier of the team chat room, useful when sending a chat message ({@link GeneralsApi#sendChatMessage(String, String)}).
     * Can be null if there are no teams in the game.
     */
    @JsonProperty("team_chat_room")
    private String teamChatRoom;
    /**
     * Array of player names, indexed by player index.
     */
    private String[] usernames;
    /**
     * Array if team identifiers of each player, indexed by player index. Can be null if there are no teams/
     */
    private Integer[] teams;

}
