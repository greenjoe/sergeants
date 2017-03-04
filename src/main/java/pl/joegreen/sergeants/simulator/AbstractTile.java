package pl.joegreen.sergeants.simulator;

abstract class AbstractTile implements Tile {

    protected final int tileIndex;

    AbstractTile(int tileIndex) {
        this.tileIndex = tileIndex;
    }

    @Override
    public int getTileIndex() {
        return tileIndex;
    }


}
