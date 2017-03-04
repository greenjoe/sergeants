package pl.joegreen.sergeants.simulator;

import org.junit.Assert;
import org.junit.Test;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.Field;
import pl.joegreen.sergeants.framework.model.GameResult;
import pl.joegreen.sergeants.framework.model.GameStarted;
import pl.joegreen.sergeants.framework.model.GameState;

import java.util.Optional;


public class SimulatorTest {

    @Test
    public void testCreateMap() throws Exception {
        GameMap map = SimulatorFactory.createMap(getClass().getResource("/replay.json").getFile());
        Assert.assertNotNull(map);
        for (Tile tile : map.getTiles()) {
            Assert.assertNotNull(tile);
        }
        Assert.assertEquals(map.getTiles().length, 324);
        // http://generals.io/replays/H_Zz-V5Ix
    }

    @Test
    public void testFullGame() {
        GameMap gameMap = SimulatorFactory.createTestMap();
        Tile[] tiles = gameMap.getTiles();
        Simulator server = SimulatorFactory.of(gameMap, 100, DoNothingBot::new, AttackGeneralBot::new);
        Optional<Player> winner = server.start();
        Assert.assertTrue(winner.isPresent());
        AttackGeneralBot bot = (AttackGeneralBot) winner.get().getBot();
        Assert.assertEquals(0, bot.enemyArmySize);//type is visible but army size is not, yes it should be fixed

        Assert.assertEquals(2, bot.gameStarted.getUsernames().length);
        Assert.assertEquals(GameResult.Result.WON, bot.gameResult.getResult());

        Assert.assertEquals(City.class, tiles[0].getClass());
        Assert.assertEquals(24, tiles[0].getArmySize());
        Assert.assertEquals(1, tiles[0].getPlayerIndex());

        Assert.assertEquals(City.class, tiles[1].getClass());
        Assert.assertEquals(2, tiles[1].getArmySize());
        Assert.assertEquals(1, tiles[1].getPlayerIndex());

        Assert.assertEquals(Empty.class, tiles[3].getClass());
        Assert.assertEquals(17, tiles[3].getArmySize());
        Assert.assertEquals(1, tiles[3].getPlayerIndex());

        Assert.assertEquals(General.class, tiles[8].getClass());
        Assert.assertEquals(3, tiles[8].getArmySize());
        Assert.assertEquals(1, tiles[8].getPlayerIndex());
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
        private GameResult gameResult;
        private int enemyArmySize;


        AttackGeneralBot(Actions actions) {
            this.actions = actions;
        }

        @Override
        public void onGameStarted(GameStarted gameStarted) {
            this.gameStarted = gameStarted;
        }

        @Override
        public void onGameStateUpdate(GameState newGameState) {
            if (newGameState.getTurn() == 3) {
                enemyArmySize = newGameState.getTwoDimensionalArrayOfFields()[0][0].asVisibleField().getArmy();
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
            this.gameResult = gameResult;
        }
    }
}
