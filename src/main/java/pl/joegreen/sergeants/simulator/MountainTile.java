package pl.joegreen.sergeants.simulator;

import java.util.Optional;

import static pl.joegreen.sergeants.simulator.TerrainType.TILE_FOG_OBSTACLE;
import static pl.joegreen.sergeants.simulator.TerrainType.TILE_MOUNTAIN;


class MountainTile extends AbstractTile {

    MountainTile(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public TerrainType getTerrainType(boolean visible) {
        return visible ? TILE_MOUNTAIN : TILE_FOG_OBSTACLE;
    }

    @Override
    public int getArmySize() {
        return 0;
    }

    @Override
    public Optional<Integer> getOwnerPlayerIndex() {
        return Optional.empty();
    }

    @Override
    public int moveFrom(boolean half) {
        throw new IllegalStateException("Can not move from mountian. Tile index:" + tileIndex);
    }

    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        throw new IllegalStateException("Can not move to mountian. Tile index:" + tileIndex);
    }
}
