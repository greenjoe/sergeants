package pl.joegreen.sergeants.framework.model;

import java.util.Optional;


public class VisibleField extends Field {
    private final int army;
    private final boolean city;
    private final boolean general;
    private final Optional<Integer> ownerIndex;


    public VisibleField(GameStateFieldContext gameStateContext, Position position, FieldTerrainType terrainType, Optional<Integer> ownerIndex, int army, boolean city, boolean general) {
        super(gameStateContext, position, terrainType);
        this.ownerIndex = ownerIndex;
        this.army = army;
        this.city = city;
        this.general = general;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isObstacle() {
        return getTerrainType().equals(FieldTerrainType.MOUNTAIN);
    }

    @Override
    public VisibleField asVisibleField() {
        return this;
    }

    /**
     * Index of player that owns the field.
     */
    public Optional<Integer> getOwnerIndex() {
        return ownerIndex;
    }

    public boolean isOwnedBy(int playerIndex){
        return Optional.of(playerIndex).equals(ownerIndex);
    }

    public boolean isOwnedByMe() {
        return isOwnedBy(gameStateContext.getMyPlayerIndex());
    }

    public boolean isOwnedByMyTeam() {
        return ownerIndex.isPresent() && gameStateContext.getMyTeamIndexes().contains(ownerIndex.get());
    }

    /**
     * Returns true if field has an owner but owner is not in the same team as bot.
     */
    public boolean isOwnedByEnemy(){
        return hasOwner() && !isOwnedByMyTeam();
    }

    /**
     * Returns true if field is represented with a white square in the brower (not mountain, not city, no owner).
     */
    public boolean isBlank(){
        return !hasOwner() && !isObstacle() && !isCity();
    }

    public boolean hasOwner() {
        return ownerIndex.isPresent();
    }

    public int getArmy() {
        return army;
    }

    public boolean isGeneral() {
        return general;
    }
    public boolean isCity() {
        return city;
    }


    @Override
    public String toString() {
        return String.format("VisibleField[position=%s, terrain=%s, army=%s, owner=%s, city=%s, general=%s]", getPosition(), getTerrainType(), getArmy(), getOwnerIndex(), isCity(), isGeneral());
    }
}
