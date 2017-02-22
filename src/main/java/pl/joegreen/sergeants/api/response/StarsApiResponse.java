package pl.joegreen.sergeants.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class StarsApiResponse {
    /**
     * Stars in 1v1 games.
     */
    private double duel;
    /**
     * Stars in FFA games.
     */
    private double ffa;
    /**
     * Stars in 2v2 games.
     */
    @JsonProperty("2v2")
    private double twoVsTwo;
}
