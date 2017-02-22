package pl.joegreen.sergeants.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class RanksApiResponse {

    /**
     * Ranking in 1v1 games.
     */
    private int duel;

    /**
     * Ranking in FFA games.
     */
    private int ffa;

    /**
     * Ranking in 2v2 games.
     */
    @JsonProperty("2v2")
    private int twoVsTwo;

}
