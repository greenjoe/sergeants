package pl.joegreen.sergeants.framework.model;

import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
public class Position {
    int row;
    int col;

    public static Position fromIndex(int index, int columns) {
        return new Position(index / columns, index % columns);
    }

    public int toIndex(int columns) {
        return getCol() + getRow() * columns;
    }

    public boolean isVisibleFrom(Position otherPosition) {
        return Math.abs(row - otherPosition.row) <= 1 && Math.abs(col - otherPosition.col) <= 1;
    }

    public boolean isMovableFrom(Position otherPosition) {
        return Math.abs(row - otherPosition.row) + Math.abs(col - otherPosition.col) == 1;
    }
}
