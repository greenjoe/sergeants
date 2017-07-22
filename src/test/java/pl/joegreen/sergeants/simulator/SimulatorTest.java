package pl.joegreen.sergeants.simulator;

import org.junit.Test;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.VisibleField;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static pl.joegreen.sergeants.simulator.SimulatorConfiguration.configuration;

public class SimulatorTest {

    public static final int MANY_TURNS = 1000;

    @Test
    public void test2x2MapNoMovement() throws Exception {
        BotInstanceCatcher<DoNothingBot> firstBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);
        BotInstanceCatcher<DoNothingBot> secondBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);
        int maxTurns = 5;
        Simulator simulator = SimulatorFactory.of(new GameMap(new Tile[]{
                new GeneralTile(0, 0), new EmptyTile(1),
                new EmptyTile(2), new GeneralTile(3, 1)
        }, 2, 2), configuration().withMaxTurns(maxTurns), firstBotProvider, secondBotProvider);
        simulator.start();

        List<GameState> gameStatesReceivedByFirstBot = firstBotProvider.lastCreatedBot.receivedGameStates;
        GameState lastGameStateReceivedByFirstBot = gameStatesReceivedByFirstBot.get(gameStatesReceivedByFirstBot.size() - 1);

        assertEquals(4, lastGameStateReceivedByFirstBot.getVisibleFields().size());
        assertEquals(1, lastGameStateReceivedByFirstBot.getVisibleFields().stream().filter(VisibleField::isOwnedByMe).count());
        assertEquals(turnToSecondHalfTick(maxTurns), lastGameStateReceivedByFirstBot.getTurn());
    }

    @Test
    public void test2x2CityTakeover() throws Exception {
        GameMap gameMap = new GameMap(new Tile[]{
                new GeneralTile(0, 0), new CityTile(1, 5),
                new EmptyTile(2), new GeneralTile(3, 1)
        }, 2, 2);
        BotInstanceCatcher<TestBot> conquerorBotProvider = new BotInstanceCatcher<>((actions) -> new TestBot() {
            @Override
            public void onGameStateUpdate(GameState newGameState) {
                super.onGameStateUpdate(newGameState);
                if (newGameState.getTurn() == turnToFistHalfTick(7)) {
                    actions.move(0, 1);
                }
            }
        });
        int maxTurns = 7;
        BotInstanceCatcher<DoNothingBot> doNothingBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);

        Simulator simulator = SimulatorFactory.of(gameMap, configuration().withMaxTurns(maxTurns), conquerorBotProvider, doNothingBotProvider);
        simulator.start();
        List<GameState> gameStatesReceivedByConqueror = conquerorBotProvider.lastCreatedBot.receivedGameStates;
        GameState lastGameStateReceivedByByConqueror = gameStatesReceivedByConqueror.get(gameStatesReceivedByConqueror.size() - 1);

        List<VisibleField> fieldsOwnedByConqueror = lastGameStateReceivedByByConqueror.getVisibleFields().stream().filter(VisibleField::isOwnedByMe).collect(Collectors.toList());
        assertEquals(2, fieldsOwnedByConqueror.size());
        int conquerorGeneralArmy = fieldsOwnedByConqueror.stream().filter(VisibleField::isGeneral).findFirst().get().getArmy();
        assertEquals(2, conquerorGeneralArmy);
        int cityArmy = fieldsOwnedByConqueror.stream().filter(VisibleField::isCity).findFirst().get().getArmy();
        assertEquals(2, cityArmy);
        assertEquals(turnToSecondHalfTick(maxTurns), lastGameStateReceivedByByConqueror.getTurn());
    }

    @Test
    public void test2x2GeneralTakeoverGameOver() throws Exception {
        GameMap gameMap = new GameMap(new Tile[]{
                new GeneralTile(0, 0), new CityTile(1, 1, Optional.of(0)),
                new CityTile(2, 100, Optional.of(0)), new GeneralTile(3, 1)
        }, 2, 2);
        BotInstanceCatcher<TestBot> conquerorBotProvider = new BotInstanceCatcher<>((actions) -> new TestBot() {
            @Override
            public void onGameStateUpdate(GameState newGameState) {
                super.onGameStateUpdate(newGameState);
                if (newGameState.getTurn() == turnToFistHalfTick(1)) {
                    actions.move(2, 3);
                }
            }
        });
        BotInstanceCatcher<DoNothingBot> doNothingBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);

        Simulator simulator = SimulatorFactory.of(gameMap, configuration().withMaxTurns(MANY_TURNS), conquerorBotProvider, doNothingBotProvider);
        simulator.start();
        List<GameState> gameStatesReceivedByConqueror = conquerorBotProvider.lastCreatedBot.receivedGameStates;
        GameState lastGameStateReceivedByByConqueror = gameStatesReceivedByConqueror.get(gameStatesReceivedByConqueror.size() - 1);

        List<VisibleField> fieldsOwnedByConqueror = lastGameStateReceivedByByConqueror.getVisibleFields().stream().filter(VisibleField::isOwnedByMe).collect(Collectors.toList());
        assertEquals(4, fieldsOwnedByConqueror.size());;
        int conqueredGeneralArmy = fieldsOwnedByConqueror.stream().filter(visibleField -> visibleField.getIndex() == 3).findFirst().get().getArmy();
        assertEquals(99, conqueredGeneralArmy);
        assertEquals(turnToSecondHalfTick(1), lastGameStateReceivedByByConqueror.getTurn());
    }



    private static int turnToSecondHalfTick(int turn) {
        return turn * 2;
    }

    private static int turnToFistHalfTick(int turn) {
        return turn * 2 - 1;
    }

}
