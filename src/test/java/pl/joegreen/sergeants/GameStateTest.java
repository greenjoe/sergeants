package pl.joegreen.sergeants;

import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;
import com.google.common.collect.ImmutableList;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.Player;
import pl.joegreen.sergeants.framework.model.Position;
import org.hamcrest.Matchers;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GameStateTest {

    @Test
    public void shouldCreateGameState() {
        GameStartApiResponse gameStartApiResponse = new GameStartApiResponse(
                1,
                "replayId",
                "chatRoom",
                "teamChatRoom",
                new String[]{"Anonymous", "username2", "username3"},
                new Integer[]{2, 2, 3}

        );
        GameUpdateApiResponse gameUpdateApiResponse = new GameUpdateApiResponse(
                1,
                new int[]{0, 10, 2, 2, 4, 3, 0, 0, 1, 2, -1, -4},
                new int[]{0, 1, 0},
                new int[]{1, -1, -1},
                0,
                new ScoreApiResponse[]{
                        new ScoreApiResponse(1, 1, 0, false),
                        new ScoreApiResponse(2, 2, 1, false),
                        new ScoreApiResponse(0, 0, 2, true)
                }
                , new double[]{
                42, 0, 0
        }
        );
        GameState gameState = GameState.createInitialGameState(gameStartApiResponse, gameUpdateApiResponse);
        assertEquals(1, gameState.getMyPlayerIndex());
        assertEquals("chatRoom", gameState.getChatRoom());
        assertEquals("teamChatRoom", gameState.getTeamChatRoom());
        ImmutableList<Player> players = gameState.getPlayers();
        assertEquals(ImmutableList.of(
                new Player(0, "Anonymous", 2, 1, 1, false),
                new Player(1, "username2", 2, 2, 2, false),
                new Player(2, "username3", 3, 0, 0, true)

        ), players);
        GameState.VisibleField field00 = gameState.getFieldsMap().get(new Position(0 ,0)).asVisibleField();
        assertEquals(new Position(0, 0), field00.getPosition());
        assertEquals(4, field00.getArmy());
        assertTrue(field00.isCity());
        assertFalse(field00.isObstacle());
        assertFalse(field00.isGeneral());
        assertFalse(field00.isBlank());
        assertTrue(field00.isOwnedBy(1));

        GameState.VisibleField field01 = gameState.getFieldsMap().get(new Position(0 ,1)).asVisibleField();
        assertEquals(new Position(0, 1), field01.getPosition());
        assertEquals(3, field01.getArmy());
        assertFalse(field01.isCity());
        assertFalse(field01.isObstacle());
        assertTrue(field01.isGeneral());
        assertFalse(field01.isBlank());
        assertTrue(field01.isOwnedBy(2));

        GameState.VisibleField field10 = gameState.getFieldsMap().get(new Position(1 ,0)).asVisibleField();
        assertEquals(new Position(1, 0), field10.getPosition());
        assertEquals(0, field10.getArmy());
        assertFalse(field10.isCity());
        assertFalse(field10.isObstacle());
        assertFalse(field10.isGeneral());
        assertTrue(field10.isBlank());
        assertFalse(field10.hasOwner());

        GameState.Field field11 = gameState.getFieldsMap().get(new Position(1 ,1));
        assertEquals(new Position(1, 1), field11.getPosition());
        assertTrue(field11.isObstacle());
        assertFalse(field11.isVisible());

        assertThat(field00.getNeighbours(), Matchers.hasSize(2));
        assertThat(field00.getVisibleNeighbours(), Matchers.hasSize(2));

        assertThat(field10.getNeighbours(), Matchers.hasSize(2));
        assertThat(field10.getVisibleNeighbours(), Matchers.hasSize(1));


    }
}
