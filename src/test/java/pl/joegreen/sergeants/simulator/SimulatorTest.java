package pl.joegreen.sergeants.simulator;

import org.junit.Test;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.VisibleField;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimulatorTest {
    @Test
    public void test2x2MapNoMovement() throws Exception {
        BotInstanceCatcher<DoNothingBot> firstBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);
        BotInstanceCatcher<DoNothingBot> secondBotProvider = new BotInstanceCatcher<>(DoNothingBot::new);
        int maxTurns = 5;
        Simulator simulator = SimulatorFactory.of(new GameMap(new Tile[]{
                new GeneralTile(0, 0), new EmptyTile(1), new EmptyTile(2), new GeneralTile(3, 1)
        }, 2, 2), maxTurns, firstBotProvider, secondBotProvider);
        simulator.start();

        List<GameState> gameStatesReceivedByFirstBot = firstBotProvider.lastCreatedBot.receivedGameStates;
        GameState lastGameStateReceivedByFirstBot = gameStatesReceivedByFirstBot.get(gameStatesReceivedByFirstBot.size() - 1);

        assertEquals(4, lastGameStateReceivedByFirstBot.getVisibleFields().size());
        assertEquals(1, lastGameStateReceivedByFirstBot.getVisibleFields().stream().filter(VisibleField::isOwnedByMe).count());
        assertEquals(2 * maxTurns, lastGameStateReceivedByFirstBot.getTurn());
    }


}
