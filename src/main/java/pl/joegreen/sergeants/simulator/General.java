package pl.joegreen.sergeants.simulator;

import java.util.Optional;

class General extends City {


    General(int tileIndex, int playerIndex) {
        super(tileIndex, 1, playerIndex);
    }

    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        int oldPlayerIndex = this.getPlayerIndex();
        if (this.playerIndex == attackerPlayerIndex) {
            armySize += attackerArmySize;
        } else if (attackerArmySize > armySize) {
            int newArmySize = attackerArmySize - armySize;
            tiles[tileIndex] = new City(tileIndex, newArmySize, attackerPlayerIndex);
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
}
