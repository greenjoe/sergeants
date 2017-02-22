package pl.joegreen.sergeants.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ScoreApiResponse {
    /**
     * Number of map tiles owned.
     */
    private int tiles;
    /**
     * Total army size.
     */
    private int total;
    /**
     * Player index.
     */
    @JsonProperty("i")
    private int index;
    /**
     * True if player already lost.
     */
    private boolean dead;

}
