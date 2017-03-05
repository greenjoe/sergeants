package pl.joegreen.sergeants.simulator;

import java.util.Optional;

class GeneralTile extends AbstractTile {

    private static final int INITIAL_ARMY_SIZE = 1;

    GeneralTile(int tileIndex, int playerIndex) {
        super(tileIndex, INITIAL_ARMY_SIZE, playerIndex);
    }


    @Override
    public Optional<PlayerKilled> moveTo(int attackerArmySize, int attackerPlayerIndex, Tile[] tiles) {
        if (isOwnedBy(attackerPlayerIndex)) {
            armySize += attackerArmySize;
        } else if (attackerArmySize > armySize) {
            int newArmySize = attackerArmySize - armySize;
            tiles[tileIndex] = new CityTile(tileIndex, newArmySize, Optional.of(attackerPlayerIndex));
            return Optional.of(new PlayerKilled(this.playerIndex.get(), attackerPlayerIndex));
        } else {
            armySize -= attackerArmySize;
        }
        return Optional.empty();
    }

    @Override
    public TerrainType getTerrainType(boolean visible) {
        return visible ? TerrainType.playerOwnedTerrain(playerIndex.get()): TerrainType.TILE_FOG;
    }

    @Override
    public void turn() {
        armySize++;
    }

}
