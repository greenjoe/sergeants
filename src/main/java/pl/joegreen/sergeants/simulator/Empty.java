package pl.joegreen.sergeants.simulator;

import java.util.Optional;

class Empty extends AbstractTile {

    protected int playerIndex = TILE_EMPTY;
    protected int armySize = 0;

    public Empty(int tileIndex, int armySize, int playerIndex) {
        super(tileIndex);
        this.armySize = armySize;
        this.playerIndex = playerIndex;
    }

    public Empty(int tileIndex) {
        super(tileIndex);
    }

    @Override
    public int getArmySize() {
        return armySize;
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? playerIndex : TILE_FOG;
    }

    @Override
    public int moveFrom(boolean half) {
        int moveSize = armySize - (half ? armySize / 2 : 1);
        armySize -= moveSize;
        return moveSize;
    }

    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        if (this.playerIndex == attackerPlayerIndex) {
            armySize += attackerArmySize;
        } else if (attackerArmySize > armySize) {
            armySize = attackerArmySize - armySize;
            this.playerIndex = attackerPlayerIndex;
        } else {
            armySize -= attackerArmySize;
        }
        return Optional.empty();
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
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
