package pl.joegreen.sergeants.simulator;

import java.util.Optional;

class GeneralTile extends AbstractTile {

    private static final int INITIAL_ARMY_SIZE = 1;

    GeneralTile(int tileIndex, int playerIndex) {
        super(tileIndex, INITIAL_ARMY_SIZE, playerIndex);
    }

    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        if (this.playerIndex == attackerPlayerIndex) {
            armySize += attackerArmySize;
        } else if (attackerArmySize > armySize) {
            int newArmySize = attackerArmySize - armySize;
            tiles[tileIndex] = new CityTile(tileIndex, newArmySize, attackerPlayerIndex);
            return Optional.of(new PlayerKilled(this.playerIndex, attackerPlayerIndex));
        } else {
            armySize -= attackerArmySize;
        }
        return Optional.empty();
    }

    @Override
    public int getTerrain(boolean visible) {
        return visible ? playerIndex : TILE_FOG;
    }

    @Override
    public void turn() {
        armySize++;
    }

}
