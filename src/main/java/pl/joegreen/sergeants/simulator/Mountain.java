package pl.joegreen.sergeants.simulator;

class Mountain extends AbstractTile {

    public Mountain(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? TILE_MOUNTAIN : TILE_FOG_OBSTACLE;
    }
}
