package pl.joegreen.sergeants.simulator;

class Mountain extends AbstractTile {

    public Mountain(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public int getTerrain() {
        return TILE_MOUNTAIN;
    }
}
