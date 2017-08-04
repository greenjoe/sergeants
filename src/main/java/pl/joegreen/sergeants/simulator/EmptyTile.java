package pl.joegreen.sergeants.simulator;

import java.util.Optional;

import static pl.joegreen.sergeants.simulator.TerrainType.TILE_FOG;

class EmptyTile extends AbstractTile {

    EmptyTile(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public TerrainType getTerrainType(boolean visible) {
        return visible ? getOwnerPlayerIndex().map(TerrainType::playerOwnedTerrain).orElse(TerrainType.TILE_EMPTY) : TILE_FOG;
    }


    @Override
    public void round() {
        if (hasOwner()) {
            armySize++;
        }
    }

    @Override
    public void transfer(int playerIndex) {
        armySize = (armySize + 1) / 2;
        this.playerIndex = Optional.of(playerIndex);
    }
}
