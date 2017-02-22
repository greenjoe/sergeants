package pl.joegreen.sergeants;

import pl.joegreen.sergeants.api.GeneralsApi;
import pl.joegreen.sergeants.api.listener.NoArgsListener;
import pl.joegreen.sergeants.api.listener.OneArgListener;
import pl.joegreen.sergeants.api.response.GameLostApiResponse;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;
import pl.joegreen.sergeants.api.test.FakeSocket;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class GeneralsApiTest {
    private FakeSocket fakeSocket = new FakeSocket();
    private GeneralsApi generalsApi = GeneralsApi.createWithCustomSocket(fakeSocket);

    @Test
    public void shouldSetUsername() {
        generalsApi.setUsername("id", "name");
        assertFakeSocketReceivedOnlyOneEvent("set_username", "id", "name");
    }

    @Test
    public void shouldJoinFreeForAllQueue() {
        generalsApi.joinFreeForAllQueue("id");
        assertFakeSocketReceivedOnlyOneEvent("play", "id");
    }

    @Test
    public void shouldAttackAll() {
        generalsApi.attack(0, 1, false);
        assertFakeSocketReceivedOnlyOneEvent("attack", 0, 1, false);
    }

    @Test
    public void shouldAttackHalf() {
        generalsApi.attack(0, 1, true);
        assertFakeSocketReceivedOnlyOneEvent("attack", 0, 1, true);
    }

    @Test
    public void shouldSendChatMessage() {
        generalsApi.sendChatMessage("room", "text");
        assertFakeSocketReceivedOnlyOneEvent("chat_message", "room", "text");
    }

    @Test
    public void shouldConnect() {
        generalsApi.connect();
        assertTrue(generalsApi.isConnected());
    }

    @Test
    public void shouldDisconnect() {
        generalsApi.connect();
        generalsApi.disconnect();
        assertFalse(generalsApi.isConnected());
    }

    @Test
    public void shouldNotifyOnSetUsernameError() {
        OneArgListener<String> listener = mockOneArgListener();
        generalsApi.onSetUsernameError(listener);
        fakeSocket.injectEvent(new FakeSocket.Event("error_set_username", "someError"));
        Mockito.verify(listener).onEvent("someError");
    }


    @Test
    public void shouldNotifyOnGameLost() throws JSONException {
        OneArgListener<GameLostApiResponse> listener = mockOneArgListener();
        generalsApi.onGameLost(listener);
        String eventJson = "{'killer': 1}";
        fakeSocket.injectEvent(new FakeSocket.Event("game_lost", TestUtils.asJsonOrgObject(eventJson)));
        Mockito.verify(listener).onEvent(new GameLostApiResponse(1));
    }

    @Test
    public void shouldNotifyOnGameWon() {
        NoArgsListener listener = Mockito.mock(NoArgsListener.class);
        generalsApi.onGameWon(listener);
        fakeSocket.injectEvent(new FakeSocket.Event("game_won"));
        Mockito.verify(listener).onEvent();
    }


    @Test
    public void shouldNotifyOnGameStarted() throws IOException, URISyntaxException {
        OneArgListener<GameStartApiResponse> listener = mockOneArgListener();
        generalsApi.onGameStarted(listener);
        TestUtils.readEventsFromClassPathFile("gameStartTest").forEach(fakeSocket::injectEvent);
        Mockito.verify(listener).onEvent(new GameStartApiResponse(
                1,
                "replayId",
                "chatRoom",
                "teamChatRoom",
                new String[]{"Anonymous", "username2", "username3"},
                new Integer[] {2,2,3}

        ));
    }

    @Test
    public void shouldNotifyOnGameUpdated() throws IOException, URISyntaxException {
        OneArgListener<GameUpdateApiResponse> listener = mockOneArgListener();
        generalsApi.onGameUpdated(listener);
        TestUtils.readEventsFromClassPathFile("gameUpdateTest").forEach(fakeSocket::injectEvent);
        ArgumentCaptor<GameUpdateApiResponse> gameUpdateCaptor = ArgumentCaptor.forClass(GameUpdateApiResponse.class);
        Mockito.verify(listener).onEvent(gameUpdateCaptor.capture());
        GameUpdateApiResponse update = gameUpdateCaptor.getValue();
        assertEquals(0, update.getAttackIndex());
        assertArrayEquals(new int[]{0,1,0}, update.getCitiesDiff());
        assertArrayEquals(new int[]{1, -1, -1}, update.getGenerals());
        assertArrayEquals(new int[]{0,10,2,2,1,2,0,0,1,0,-1,-4}, update.getMapDiff());
        assertArrayEquals(new double[]{41, 0,0}, update.getStars(), 0);
        assertArrayEquals(new ScoreApiResponse[] {
                new ScoreApiResponse(1, 1, 0, false),
                new ScoreApiResponse(2, 2, 1, false),
                new ScoreApiResponse(0, 0, 2, true)
        }, update.getScores());
        assertEquals(1, update.getTurn());
    }


    @Test
    public void shouldNotifyOnGameUpdate() throws IOException, URISyntaxException {
        OneArgListener<GameStartApiResponse> listener = mockOneArgListener();
        generalsApi.onGameStarted(listener);
        TestUtils.readEventsFromClassPathFile("gameStartTest").forEach(fakeSocket::injectEvent);
        Mockito.verify(listener).onEvent(new GameStartApiResponse(
                1,
                "replayId",
                "chatRoom",
                "teamChatRoom",
                new String[]{"Anonymous", "username2", "username3"},
                new Integer[] {2,2,3}

        ));
    }



    private void assertFakeSocketReceivedOnlyOneEvent(String eventName, Object... arguments) {
        assertThat(fakeSocket.getReceivedEventsByName(eventName), Matchers.contains(EventMatchers.withArguments(arguments)));
    }

    @SuppressWarnings("unchecked")
    private <T> OneArgListener<T> mockOneArgListener() {
        return Mockito.mock(OneArgListener.class);
    }







}
