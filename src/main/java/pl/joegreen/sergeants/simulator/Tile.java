package pl.joegreen.sergeants.simulator;

import java.util.Optional;

interface Tile {

    int TILE_EMPTY = -1;
    int TILE_MOUNTAIN = -2;
    int TILE_FOG = -3;
    int TILE_FOG_OBSTACLE = -4; // Cities and Mountains show up as Obstacles in the fog of war.


    default int getArmySize() {
//        throw new IllegalStateException("Can not get army size from: " + this.getClass().getSimpleName());
        return 0;
    }

    default int getPlayerIndex() {
        return -1;
//        throw new IllegalStateException("Can not get player index from: " + this.getClass().getSimpleName());
    }

    default void turn() {
    }

    default void round() {
    }

    int getTileIndex();

    default int getTerrain() {
        return TILE_EMPTY;
    }

    default int moveFrom(boolean half) {
        throw new IllegalStateException("Can not move from: " + this.getClass().getSimpleName());
    }

    default Optional<PlayerKilled> moveTo(int armySize, int playerIndex, Tile[] tiles) {
        throw new IllegalStateException("Can not move to: " + this.getClass().getSimpleName());
    }

    default void transfer(int playerIndex) {
        throw new IllegalStateException("Can not take over: " + this.getClass().getSimpleName());
    }

    default String getType() {
        return this.getClass().getSimpleName().toLowerCase();
    }
}
