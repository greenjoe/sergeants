package pl.joegreen.sergeants.simulator;

import java.util.Optional;

interface Tile {


    /**
     * Used for the first half of mapdiff.
     * Zero means unoccupied.
     */
    int getArmySize();

    Optional<Integer> getOwnerPlayerIndex();

    default boolean isOwnedBy(int playerIndex) {
        return getOwnerPlayerIndex().map(index -> index == playerIndex).orElse(false);
    }

    default boolean hasOwner() {
        return getOwnerPlayerIndex().isPresent();
    }

    default void turn() {
    }

    default void round() {
    }

    int getTileIndex();

    TerrainType getTerrainType(boolean visible);

    int moveFrom(boolean half);

    Optional<PlayerKilled> moveTo(int armySize, int playerIndex, Tile[] tiles);

    default void transfer(int playerIndex) {
        throw new IllegalStateException("Can not transfer over: " + this.getClass().getSimpleName());
    }
}
