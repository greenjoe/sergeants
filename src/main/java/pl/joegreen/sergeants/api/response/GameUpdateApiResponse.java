package pl.joegreen.sergeants.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import pl.joegreen.sergeants.api.util.MapPatcher;

@Value
public class GameUpdateApiResponse {
    private int turn;
    /**
     * Diff to update the array with map (tiles/armies) representation. Use {@link MapPatcher} to apply the diff.
     */
    @JsonProperty("map_diff")
    private int[] mapDiff;
    /**
     * Diff to update the array with cities representation. Use {@link MapPatcher} to apply the diff.
     */
    @JsonProperty("cities_diff")
    private int[] citiesDiff;
    /**
     * Array of indexes of generals, indexed with player index. If general of player is not visible/does not exist, contains -1 in appropriate position.
     */
    private int[] generals;
    /**
     * "the attackIndex field is unnecessary for bots -- it's used by our client to show/hide attack arrows when appropriate."
     */
    private int attackIndex;
    /**
     * Array indexed by player index, includes things shown in the score table (titles, army, is dead). Does not include stars.
     */
    private ScoreApiResponse[] scores;
    private double[] stars;
}
