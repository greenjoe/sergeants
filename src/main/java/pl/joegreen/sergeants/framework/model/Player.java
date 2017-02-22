package pl.joegreen.sergeants.framework.model;

import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
public class Player {
    Integer index;
    String username;

    /**
     * Identifier of a team that player belongs to.
     */
    Integer team;

    /**
     * Number of fields owned by player.
     */
    Integer fields;

    /**
     * Total army owned by player.
     */
    Integer army;
    //stars not yet supported
    /**
     * True if player has already lost the game.
     */
    boolean dead;
}
