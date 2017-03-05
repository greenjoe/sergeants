package pl.joegreen.sergeants.simulator;

class CityTile extends AbstractTile {

    private final int initialArmySize;

    CityTile(int tileIndex, int armySize, int playerIndex) {
        super(tileIndex, armySize, playerIndex);
        initialArmySize = armySize;
    }

    CityTile(int tileIndex, int armySize) {
        this(tileIndex, armySize, TILE_EMPTY);
    }

    @Override
    public void turn() {
        if (playerIndex != TILE_EMPTY || getArmySize() < initialArmySize) {
            armySize++;
        }
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? playerIndex : TILE_FOG_OBSTACLE;
    }

    @Override
    public void transfer(int playerIndex) {
        armySize /= 2;
        this.playerIndex = playerIndex;
    }
}
