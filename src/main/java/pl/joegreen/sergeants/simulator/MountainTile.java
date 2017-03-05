package pl.joegreen.sergeants.simulator;

import java.util.Optional;

class MountainTile extends AbstractTile {

    public MountainTile(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? TILE_MOUNTAIN : TILE_FOG_OBSTACLE;
    }

    @Override
    public int getArmySize() {
        return 0;
    }

    @Override
    public int getPlayerIndex() {
        return TILE_EMPTY;
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
