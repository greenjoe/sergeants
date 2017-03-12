package pl.joegreen.sergeants.simulator;


import java.util.Optional;

import static pl.joegreen.sergeants.simulator.TerrainType.TILE_FOG_OBSTACLE;

class CityTile extends AbstractTile {

    private final int initialArmySize;

    CityTile(int tileIndex, int armySize, Optional<Integer> playerIndex) {
        super(tileIndex, armySize, playerIndex);
        initialArmySize = armySize;
    }

    CityTile(int tileIndex, int armySize) {
        this(tileIndex, armySize, Optional.empty());
    }

    @Override
    public void turn() {
        //TOOD: city armies should grow to some extent also if they don't have owners
        if (hasOwner() || getArmySize() < initialArmySize) {
            armySize++;
        }
    }

    @Override
    public TerrainType getTerrainType(boolean visible) {
        return visible ? getOwnerPlayerIndex().map(TerrainType::playerOwnedTerrain).orElse(TerrainType.TILE_EMPTY) : TILE_FOG_OBSTACLE;
    }

    @Override
    public void transfer(int playerIndex) {
        armySize /= 2;
        this.playerIndex = Optional.of(playerIndex);
    }
}
