package pl.joegreen.sergeants.simulator;


import java.util.Optional;

import static pl.joegreen.sergeants.simulator.TerrainType.TILE_FOG_OBSTACLE;

class CityTile extends AbstractTile {

    CityTile(int tileIndex, int armySize, Optional<Integer> playerIndex) {
        super(tileIndex, armySize, playerIndex);
    }

    CityTile(int tileIndex, int armySize) {
        this(tileIndex, armySize, Optional.empty());
    }

    @Override
    public void turn() {
        if (hasOwner()) {
            armySize++;
        }
    }

    @Override
    public TerrainType getTerrainType(boolean visible) {
        return visible ? getOwnerPlayerIndex().map(TerrainType::playerOwnedTerrain).orElse(TerrainType.TILE_EMPTY) : TILE_FOG_OBSTACLE;
    }

    @Override
    public void transfer(int playerIndex) {
        armySize = (armySize + 1) / 2;
        this.playerIndex = Optional.of(playerIndex);
    }
}
