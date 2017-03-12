package pl.joegreen.sergeants.simulator;

import java.util.Optional;


abstract class AbstractTile implements Tile {

    protected final int tileIndex;
    protected int armySize = 0;
    protected Optional<Integer> playerIndex = Optional.empty();


    AbstractTile(int tileIndex, int armySize){
        this(tileIndex, armySize, Optional.empty());
    }
    AbstractTile(int tileIndex, int armySize, int playerIndex){
        this(tileIndex, armySize, Optional.of(playerIndex));
    }


    AbstractTile(int tileIndex, int armySize, Optional<Integer> playerIndex) {
        this.tileIndex = tileIndex;
        this.armySize = armySize;
        this.playerIndex = playerIndex;
    }

    AbstractTile(int tileIndex) {
        this(tileIndex, 0);
    }

    @Override
    public int getTileIndex() {
        return tileIndex;
    }

    @Override
    public int getArmySize() {
        return armySize;
    }

    @Override
    public Optional<Integer> getOwnerPlayerIndex() {
        return playerIndex;
    }

    @Override
    public int moveFrom(boolean half) {
        int moveSize = armySize - (half ? armySize / 2 : 1);
        armySize -= moveSize;
        return moveSize;
    }

    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        if (isOwnedBy(attackerPlayerIndex)) {
            armySize += attackerArmySize;
        } else if (attackerArmySize > armySize) {
            armySize = attackerArmySize - armySize;
            this.playerIndex = Optional.of(attackerPlayerIndex);
        } else {
            armySize -= attackerArmySize;
        }
        return Optional.empty();
    }


}
