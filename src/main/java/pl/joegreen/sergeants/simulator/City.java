package pl.joegreen.sergeants.simulator;

class City extends Empty {

    private final int initialArmySize;

    public City(int tileIndex, int armySize, int playerIndex) {
        super(tileIndex, armySize, playerIndex);
        initialArmySize = armySize;
    }

    public City(int tileIndex, int armySize) {
        this(tileIndex, armySize, TILE_EMPTY);
    }

    @Override
    public void turn() {
        if (playerIndex != TILE_EMPTY || getArmySize() < initialArmySize) {
            armySize++;
        }
    }

    @Override
    public void round() {
        //do nothing
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? playerIndex : TILE_FOG_OBSTACLE;
    }
}
