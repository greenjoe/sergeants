package pl.joegreen.sergeants;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.api.test.FakeSocket;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.GameCoordinator;
import pl.joegreen.sergeants.framework.model.*;
import pl.joegreen.sergeants.framework.queue.QueueConfiguration;
import pl.joegreen.sergeants.framework.user.UserConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;

public class Integration38TurnsTest {
    private FakeSocket fakeSocket = new FakeSocket();
    private GeneralsApi fakeGeneralsApi = GeneralsApi.createConnectionWithCustomSocket(fakeSocket);
    private Bot bot = Mockito.mock(Bot.class);
    private List<GameResult> gameResults;


    @Before
    public void play() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        CompletableFuture<List<GameResult>> future = GameCoordinator.playAsynchronouslyWithCustomApi(fakeGeneralsApi,
                1, actions -> bot, QueueConfiguration.customGame(true, "customGame"), UserConfiguration.idAndName("id", "name"));
        TestUtils.readEventsFromClassPathFile("38turnsTest/events").forEach(fakeSocket::injectEvent);
        Assert.assertTrue(future.isDone());
        gameResults = future.get();
    }

    @Test
    public void gameResultShouldIndicateWin() throws IOException, URISyntaxException {
        assertEquals(GameResult.Result.WON, gameResults.get(0).getResult());
    }

    @Test
    public void botShouldReceiveGameFinished() {
        Mockito.verify(bot).onGameFinished(argThat(Matchers.hasProperty("result", is(GameResult.Result.WON))));
    }

    @Test
    public void botShouldReceiveChatMessage() {
        Mockito.verify(bot).onChatMessage(new ChatMessage(ChatMessage.ChatType.GAME, "Hello, I am a bot created with the Sergeants framework: https://git.io/sergeants", "[Bot]6042d970-5"));
    }

    @Test
    public void botShouldReceiveCorrectPlayersArray() {
        GameState gameState = getLastGameStateReceivedByBot();
        ImmutableList<Player> players = gameState.getPlayers();
        assertEquals(ImmutableList.of(
                new Player(0, "Anonymous", 2, 9, 42, false),
                new Player(1, "[Bot]5ff32bf3-9", 2, 31, 56, false),
                new Player(2, "[Bot]6042d970-5", 3, 25, 50, false)),
                players);
    }


    @Test
    public void botShouldReceiveFieldsWithGenerals() {
        GameState gameState = getLastGameStateReceivedByBot();

        Position blueGeneralPosition = new Position(10, 8);
        Position redGeneralPosition = new Position(17, 6);
        GameState.VisibleField blueGeneralField = gameState.getVisibleFieldsMap().get(blueGeneralPosition);
        GameState.VisibleField redGeneralField = gameState.getVisibleFieldsMap().get(redGeneralPosition);

        assertTrue(blueGeneralField.isGeneral() && blueGeneralField.isOwnedByMe() && blueGeneralField.isOwnedByMyTeam()
                && !blueGeneralField.isCity() && !blueGeneralField.isObstacle() && blueGeneralField.getArmy() == 6);

        assertTrue(redGeneralField.isGeneral() && !redGeneralField.isOwnedByMe() && redGeneralField.isOwnedByMyTeam()
                && !redGeneralField.isCity() && !redGeneralField.isObstacle() && redGeneralField.getArmy() == 16);
    }


    @Test
    public void botShouldReceiveFieldsWithCities() {
        GameState gameState = getLastGameStateReceivedByBot();

        assertEquals(4, gameState.getVisibleFields().stream().filter(GameState.VisibleField::isCity).count());

        Position cityBelowBlueGeneralPosition = new Position(11, 8);
        GameState.VisibleField cityBelowBlueGeneralField = gameState.getVisibleFieldsMap().get(cityBelowBlueGeneralPosition);
        assertTrue(cityBelowBlueGeneralField.isCity() && !cityBelowBlueGeneralField.isGeneral()
                && !cityBelowBlueGeneralField.hasOwner() && !cityBelowBlueGeneralField.isOwnedByMyTeam() && !cityBelowBlueGeneralField.isObstacle()
                && !cityBelowBlueGeneralField.isBlank() && cityBelowBlueGeneralField.getArmy() == 49);
    }

    @Test
    public void botShouldReceiveCorrectMapSize() {
        GameState gameState = getLastGameStateReceivedByBot();
        assertEquals(23, gameState.getColumns());
        assertEquals(19, gameState.getRows());
        assertEquals(23 * 19, gameState.getFields().size());
    }

    @Test
    public void botShouldReceiveCorrectNumberOfVisibleAndNonVisibleFields() {
        GameState gameState = getLastGameStateReceivedByBot();
        assertEquals(116, gameState.getVisibleFields().size());
        assertEquals(gameState.getColumns() * gameState.getRows() - 116, gameState.getFields().stream().filter(f -> !f.isVisible()).count());
    }

    @Test
    public void botShouldReceiveCorrectObstacleInfoAboutNonVisibleFields() {
        GameState gameState = getLastGameStateReceivedByBot();
        assertTrue(gameState.getFieldsMap().get(new Position(1, 0)).isObstacle());
        assertFalse(gameState.getFieldsMap().get(new Position(0, 0)).isObstacle());
    }

    @Test
    public void botShouldReceiveCorrectInfoAboutItsFields(){
        GameState gameState = getLastGameStateReceivedByBot();
        assertEquals(31, gameState.getVisibleFields().stream().filter(GameState.VisibleField::isOwnedByMe).count());
        GameState.VisibleField fieldWithBiggestBotArmy = gameState.getVisibleFieldsMap().get(new Position(13, 8));
        assertEquals(12, fieldWithBiggestBotArmy.getArmy());
    }

    @Test
    public void botShouldReceiveCorrectInfoAboutTeamFields(){
        GameState gameState = getLastGameStateReceivedByBot();
        assertEquals(40, gameState.getVisibleFields().stream().filter(GameState.VisibleField::isOwnedByMyTeam).count());
        GameState.VisibleField fieldWithBiggestTeamMateArmy = gameState.getVisibleFieldsMap().get(new Position(14, 11));
        assertEquals(18, fieldWithBiggestTeamMateArmy.getArmy());
    }

    @Test
    public void botShouldReceiveCorrectInfoAboutEnemyFields(){
        GameState gameState = getLastGameStateReceivedByBot();
        assertEquals(6, gameState.getVisibleFields().stream().filter(GameState.VisibleField::isOwnedByEnemy).count());
        GameState.VisibleField fieldWithBiggestEnemyArmy = gameState.getVisibleFieldsMap().get(new Position(7, 7));
        assertEquals(5, fieldWithBiggestEnemyArmy.getArmy());
    }



    private GameState getLastGameStateReceivedByBot() {
        ArgumentCaptor<GameState> gameStateCaptor = ArgumentCaptor.forClass(GameState.class);
        Mockito.verify(bot, Mockito.atLeastOnce()).onGameStateUpdate(gameStateCaptor.capture());
        return gameStateCaptor.getValue();
    }
}
