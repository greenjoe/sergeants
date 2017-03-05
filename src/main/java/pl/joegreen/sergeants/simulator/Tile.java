package pl.joegreen.sergeants.simulator;

import java.util.Optional;

interface Tile {

    int TILE_EMPTY = -1;
    int TILE_MOUNTAIN = -2;
    int TILE_FOG = -3;
    int TILE_FOG_OBSTACLE = -4; // Cities and Mountains show up as Obstacles in the fog of war.

    /**
     * Used for the first half of mapdiff.
     * Zero means unoccupied.
     */
    int getArmySize();

    int getPlayerIndex();

    default void turn() {
    }

    default void round() {
    }

    int getTileIndex();

    /**
     * Used for the second half of mapdiff.
     * Any non negativ number indicate playerIndex.
     * Any negative number indicate terrain type.
     * Seriously? Yes!
     */
    int getTerrain(boolean visible);

    int moveFrom(boolean half);

    Optional<PlayerKilled> moveTo(int armySize, int playerIndex, Tile[] tiles);

    default void transfer(int playerIndex) {
        throw new IllegalStateException("Can not transfer over: " + this.getClass().getSimpleName());
    }

    default String getType() {
        return this.getClass().getSimpleName().toLowerCase();
    }
}
