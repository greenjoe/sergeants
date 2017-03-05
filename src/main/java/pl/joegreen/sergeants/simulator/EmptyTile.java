package pl.joegreen.sergeants.simulator;

class EmptyTile extends AbstractTile {

    public EmptyTile(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? playerIndex : TILE_FOG;
    }


    @Override
    public void round() {
        if (playerIndex >= 0) {
            armySize++;
        }
    }

    @Override
    public void transfer(int playerIndex) {
        armySize /= 2;
        this.playerIndex = playerIndex;
    }
}
