package pl.joegreen.sergeants.simulator;

import org.junit.Assert;
import org.junit.Test;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.*;

import java.util.Optional;


public class SimulatorTest {

    @Test
    public void testCreateMap() throws Exception {
        GameMap map = SimulatorFactory.createMapFromReplayFile(getClass().getResource("/gioreplay8.json").getFile());
        Assert.assertNotNull(map);
        for (Tile tile : map.getTiles()) {
            Assert.assertNotNull(tile);
        }
        Assert.assertEquals(map.getTiles().length, 504);
    }

    @Test
    public void testFullGame() {
        Tile[] tiles = new Tile[]{
                new GeneralTile(0, 0), new CityTile(1, 11), new MountainTile(2),
                new EmptyTile(3), new EmptyTile(4), new EmptyTile(5),
                new EmptyTile(6), new EmptyTile(7), new GeneralTile(8, 1)
        };
        GameMap gameMap = new GameMap(tiles, 3, 3);
        Simulator server = SimulatorFactory.of(gameMap, DoNothingBot::new, AttackGeneralBot::new);
        server.setMaxTurns(200);
        Optional<Integer> winner = server.start();
        Assert.assertTrue(winner.isPresent());

        Assert.assertEquals(CityTile.class, tiles[0].getClass());
        Assert.assertEquals(24, tiles[0].getArmySize());
        Assert.assertEquals(Optional.of(1), tiles[0].getOwnerPlayerIndex());

        Assert.assertEquals(CityTile.class, tiles[1].getClass());
        Assert.assertEquals(2, tiles[1].getArmySize());
        Assert.assertEquals(Optional.of(1), tiles[1].getOwnerPlayerIndex());

        Assert.assertEquals(EmptyTile.class, tiles[3].getClass());
        Assert.assertEquals(17, tiles[3].getArmySize());
        Assert.assertEquals(Optional.of(1), tiles[3].getOwnerPlayerIndex());

        Assert.assertEquals(GeneralTile.class, tiles[8].getClass());
        Assert.assertEquals(3, tiles[8].getArmySize());
        Assert.assertEquals(Optional.of(1), tiles[8].getOwnerPlayerIndex());
    }

    private class DoNothingBot implements Bot {
        private final Actions actions;

        public DoNothingBot(Actions actions) {
            this.actions = actions;
        }

        @Override
        public void onGameStateUpdate(GameState newGameState) {
            actions.move(0, 3);
        }
    }

    private class AttackGeneralBot implements Bot {
        private final Actions actions;
        private GameStarted gameStarted;


        AttackGeneralBot(Actions actions) {
            this.actions = actions;
        }

        @Override
        public void onGameStarted(GameStarted gameStarted) {
            Assert.assertEquals(2, gameStarted.getUsernames().length);
        }

        @Override
        public void onGameStateUpdate(GameState newGameState) {
            if (newGameState.getTurn() == 0) {
                Field enemyGeneral = newGameState.getTwoDimensionalArrayOfFields()[0][0];
                Field foggedCity = newGameState.getTwoDimensionalArrayOfFields()[0][1];
                Field foggedMountain = newGameState.getTwoDimensionalArrayOfFields()[0][2];
                Assert.assertEquals(FieldTerrainType.FOG, enemyGeneral.getTerrainType());
                Assert.assertEquals(FieldTerrainType.FOG_OBSTACLE, foggedCity.getTerrainType());
                Assert.assertEquals(FieldTerrainType.FOG_OBSTACLE, foggedMountain.getTerrainType());

                int[] visibleIndexes = newGameState.getVisibleFields().stream().mapToInt(Field::getIndex).sorted().toArray();
                Assert.assertArrayEquals(new int[]{4, 5, 7, 8}, visibleIndexes);
            }
            if (newGameState.getTurn() == 24) {
                VisibleField enemyGeneral = newGameState.getTwoDimensionalArrayOfFields()[0][0].asVisibleField();
                VisibleField city = newGameState.getTwoDimensionalArrayOfFields()[0][1].asVisibleField();
                VisibleField mountain = newGameState.getTwoDimensionalArrayOfFields()[0][2].asVisibleField();
                Assert.assertTrue(enemyGeneral.isGeneral());
                Assert.assertTrue(city.isCity());
                Assert.assertEquals(FieldTerrainType.MOUNTAIN, mountain.getTerrainType());
            }

            Field field = newGameState.getTwoDimensionalArrayOfFields()[2][2];
            if (field.asVisibleField().getArmy() == 12) {
                actions.move(8, 7);
                actions.move(7, 4);
                actions.move(4, 1);
                actions.move(1, 0);
            }
        }

        @Override
        public void onGameFinished(GameResult gameResult) {
            Assert.assertEquals(GameResult.Result.WON, gameResult.getResult());
        }
    }
}
