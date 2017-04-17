package pl.joegreen.sergeants.simulator;

import org.junit.Assert;
import org.junit.Test;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.model.*;

import java.util.Optional;

import static org.junit.Assert.assertEquals;


public class SimulatorFullGameTest {

    @Test
    public void testFullGame() {
        Tile[] tiles = new Tile[]{
                new GeneralTile(0, 0), new CityTile(1, 11), new MountainTile(2),
                new EmptyTile(3), new EmptyTile(4), new EmptyTile(5),
                new EmptyTile(6), new EmptyTile(7), new GeneralTile(8, 1)
        };
        GameMap gameMap = new GameMap(tiles, 3, 3);
        BotInstanceCatcher<AttackGeneralBot> attackGeneralBotProvider = new BotInstanceCatcher<>(AttackGeneralBot::new);
        Simulator server = SimulatorFactory.of(gameMap, 200, DoSameMoveBot.forMove(0, 3), attackGeneralBotProvider);
        Optional<Integer> winner = server.start();
        Assert.assertTrue(winner.isPresent());

        AttackGeneralBot attackGeneralBot = attackGeneralBotProvider.lastCreatedBot;
        GameState attackBotStateTurn0 = attackGeneralBot.receivedGameStates.get(0);
        attackGeneralBot.receivedGameStates.stream().map(GameState::getTurn).forEach(System.out::println);
        checkAttackGameBotStateTurn0(attackBotStateTurn0);
        GameState attackBotStateTurn24 = attackGeneralBot.receivedGameStates.get(25); //FIXME: bot gets two updates with turn 0, issue #10
        checkAttackGameBotStateTurn24(attackBotStateTurn24);
        checkFinalGameState(tiles);
    }

    private void checkAttackGameBotStateTurn0(GameState gameState) {
        assertEquals(0, gameState.getTurn());
        Field enemyGeneral = gameState.getTwoDimensionalArrayOfFields()[0][0];
        Field foggedCity = gameState.getTwoDimensionalArrayOfFields()[0][1];
        Field foggedMountain = gameState.getTwoDimensionalArrayOfFields()[0][2];
        assertEquals(FieldTerrainType.FOG, enemyGeneral.getTerrainType());
        assertEquals(FieldTerrainType.FOG_OBSTACLE, foggedCity.getTerrainType());
        assertEquals(FieldTerrainType.FOG_OBSTACLE, foggedMountain.getTerrainType());

        int[] visibleIndexes = gameState.getVisibleFields().stream().mapToInt(Field::getIndex).sorted().toArray();
        Assert.assertArrayEquals(new int[]{4, 5, 7, 8}, visibleIndexes);
    }

    private void checkAttackGameBotStateTurn24(GameState gameState) {
        assertEquals(24, gameState.getTurn());
        VisibleField enemyGeneral = gameState.getTwoDimensionalArrayOfFields()[0][0].asVisibleField();
        VisibleField city = gameState.getTwoDimensionalArrayOfFields()[0][1].asVisibleField();
        VisibleField mountain = gameState.getTwoDimensionalArrayOfFields()[0][2].asVisibleField();
        Assert.assertTrue(enemyGeneral.isGeneral());
        Assert.assertTrue(city.isCity());
        assertEquals(FieldTerrainType.MOUNTAIN, mountain.getTerrainType());
    }

    private void checkFinalGameState(Tile[] tiles) {
        assertEquals(CityTile.class, tiles[0].getClass());
        assertEquals(10, tiles[0].getArmySize());
        assertEquals(Optional.of(1), tiles[0].getOwnerPlayerIndex());

        assertEquals(CityTile.class, tiles[1].getClass());
        assertEquals(2, tiles[1].getArmySize());
        assertEquals(Optional.of(1), tiles[1].getOwnerPlayerIndex());

        assertEquals(EmptyTile.class, tiles[3].getClass());
        assertEquals(12, tiles[3].getArmySize());
        assertEquals(Optional.of(1), tiles[3].getOwnerPlayerIndex());

        assertEquals(GeneralTile.class, tiles[8].getClass());
        assertEquals(3, tiles[8].getArmySize());
        assertEquals(Optional.of(1), tiles[8].getOwnerPlayerIndex());
    }


    private class AttackGeneralBot extends TestBot {
        private final Actions actions;

        AttackGeneralBot(Actions actions) {
            this.actions = actions;
        }

        @Override
        public void onGameStarted(GameStarted gameStarted) {
            assertEquals(2, gameStarted.getUsernames().length);
        }

        @Override
        public void onGameStateUpdate(GameState newGameState) {
            super.onGameStateUpdate(newGameState);
            actions.move(3, 4);//not a valid move, should be ignored by simulator
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
            assertEquals(GameResult.Result.WON, gameResult.getResult());
        }
    }
}
