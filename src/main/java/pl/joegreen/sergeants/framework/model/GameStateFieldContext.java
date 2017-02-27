package pl.joegreen.sergeants.framework.model;

import java.util.Set;

public interface GameStateFieldContext {
    Set<Field> getNeighbours(Field field);

    int positionToIndex(Position position);

    int getMyPlayerIndex();

    Set<Integer> getMyTeamIndexes();
}
