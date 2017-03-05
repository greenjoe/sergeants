package pl.joegreen.sergeants.simulator;

final class TerrainType {
    final int intValue;

    final static TerrainType TILE_EMPTY = new TerrainType(-1);
    final static TerrainType TILE_MOUNTAIN = new TerrainType(-2);
    final static TerrainType TILE_FOG = new TerrainType(-3);
    final static TerrainType TILE_FOG_OBSTACLE = new TerrainType(-4) /* Cities and Mountains show up as Obstacles in the fog of war.*/;

    static TerrainType playerOwnedTerrain(int playerIndex) {
        if (playerIndex < 0) {
            throw new IllegalArgumentException("Player index cannot be < 0");
        }
        return new TerrainType(playerIndex);
    }

    private TerrainType(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerrainType that = (TerrainType) o;

        return intValue == that.intValue;
    }

    @Override
    public int hashCode() {
        return intValue;
    }
}
