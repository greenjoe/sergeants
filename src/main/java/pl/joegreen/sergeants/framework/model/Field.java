package pl.joegreen.sergeants.framework.model;

import java.util.Set;
import java.util.stream.Collectors;

public class Field {
    protected final GameStateFieldContext gameStateContext;
    private final Position position;
    private final FieldTerrainType terrainType;

    public Field(GameStateFieldContext gameStateContext, Position position, FieldTerrainType terrainType) {
        this.gameStateContext = gameStateContext;
        this.position = position;
        this.terrainType = terrainType;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * Determines whether in the current round the field is visible or in the fog of war.
     * If this method returns true, {@link #asVisibleField()} method can be used to obtain additional info about this field.
     *
     * @return true if all details of this field are known, false if the field is behind the fog of war (too far to be seen)
     */
    public boolean isVisible() {
        return false;
    }

    /**
     * If the field is not visible, true indicates city or mountain, if the field is visible true indicates only mountain.
     */
    public boolean isObstacle() {
        return terrainType.equals(FieldTerrainType.FOG_OBSTACLE);
    }

    /**
     * Converts to a VisibleField instance that exposes more details about the field. Throws an exception
     * if the field is not visible, so it should be checked before calling this method with {@link Field#isVisible}.
     */
    public VisibleField asVisibleField() {
        throw new IllegalStateException("Field is not visible");
    }

    /**
     * Returns fields that can be reached from this field in one move.
     * Those fields are the field directly above, below, on the right and on the left. Method will ommit
     * fields not being inside the map, so it handles borders well.
     */
    public Set<Field> getNeighbours() {
        return gameStateContext.getNeighbours(this);
    }

    /**
     * Same as {@link Field#getNeighbours()} but filtered with {@link Field#isVisible()}.
     */
    public Set<VisibleField> getVisibleNeighbours() {
        return getNeighbours().stream()
                .filter(Field::isVisible)
                .map(Field::asVisibleField)
                .collect(Collectors.toSet());
    }

    public int getIndex() {
        return gameStateContext.positionToIndex(position);
    }

    public FieldTerrainType getTerrainType() {
        return terrainType;
    }

    @Override
    public String toString() {
        return String.format("Field[position=%s, terrain=%s]", getPosition(), terrainType);
    }
}
