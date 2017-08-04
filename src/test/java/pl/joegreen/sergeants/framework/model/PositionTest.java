package pl.joegreen.sergeants.framework.model;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PositionTest {

    @Test
    public void threeByThreeSquareShouldBeVisible() {
        Position centralPosition = new Position(1, 1);
        Stream.of(
                new Position(0, 0),
                new Position(0, 1),
                new Position(0, 2),
                new Position(1, 0),
                new Position(1, 1),
                new Position(1, 2),
                new Position(2, 0),
                new Position(2, 1),
                new Position(2, 2)
        ).forEach(otherPosition -> {
            assertTrue(centralPosition.isVisibleFrom(otherPosition));
            assertTrue(otherPosition.isVisibleFrom(centralPosition));
        });
    }


    @Test
    public void positionsTooFarShouldNotBeVisible() {
        Position centralPosition = new Position(2, 2);
        Stream.of(
                new Position(0, 0),
                new Position(10, 10),
                new Position(2, 10),
                new Position(10, 2)
        ).forEach(otherPosition -> {
            assertFalse(centralPosition.isVisibleFrom(otherPosition));
            assertFalse(otherPosition.isVisibleFrom(centralPosition));
        });
    }

    @Test
    public void fourNonDiagonalNeighboursShouldBeMovable() {
        Position centralPosition = new Position(1, 1);
        Stream.of(
                new Position(0, 1),
                new Position(1, 0),
                new Position(1, 2),
                new Position(2, 1)
        ).forEach(otherPosition -> {
            assertTrue(centralPosition.isMovableFrom(otherPosition));
            assertTrue(otherPosition.isMovableFrom(centralPosition));
        });
    }


    @Test
    public void diagonalNeighboursShouldNotBeMovable() {
        Position centralPosition = new Position(1, 1);
        Stream.of(
                new Position(0, 0),
                new Position(2, 2),
                new Position(2, 0),
                new Position(0, 2)
        ).forEach(otherPosition -> {
            assertFalse(centralPosition.isMovableFrom(otherPosition));
            assertFalse(otherPosition.isMovableFrom(centralPosition));
        });
    }

    @Test
    public void positionsTooFarAwayShouldNotBeMovable() {
        Position centralPosition = new Position(1, 1);
        Stream.of(
                new Position(1, 10),
                new Position(10, 1),
                new Position(20, 20)
        ).forEach(otherPosition -> {
            assertFalse(centralPosition.isMovableFrom(otherPosition));
            assertFalse(otherPosition.isMovableFrom(centralPosition));
        });
    }

    @Test
    public void positionShouldNotBeMovableFromItself(){
        Stream.of(
                new Position(1, 1),
                new Position(0, 0),
                new Position(0, 10)
        ).forEach(position -> {
            assertFalse(position.isMovableFrom(position));
        });
    }
}