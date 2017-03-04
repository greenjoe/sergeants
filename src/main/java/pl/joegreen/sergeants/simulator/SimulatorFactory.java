package pl.joegreen.sergeants.simulator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

/**
 * Factory class for creating simulations
 */
public class SimulatorFactory {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SafeVarargs
    static Simulator of(int maxTurns, Function<Actions, Bot>... botProviders) {
        return new Simulator(createTestMap(), maxTurns, createPlayers(botProviders));
    }

    @SafeVarargs
    static Simulator of(GameMap gameMap, int maxTurns, Function<Actions, Bot>... botProviders) {
        return new Simulator(gameMap, maxTurns, createPlayers(botProviders));
    }

    @SafeVarargs
    public static Simulator of(String jsonReplay, int maxTurns, Function<Actions, Bot>... botProviders) throws IOException {
        return of(createMap(jsonReplay), maxTurns, botProviders);
    }

    private static Player[] createPlayers(Function<Actions, Bot>[] botProviders) {
        Player[] ret = new Player[botProviders.length];
        for (int i = 0; i < botProviders.length; i++) {
            Deque<Move> moves = new ArrayDeque<>();
            Actions action = new SimulatorActions(moves);
            Bot bot = botProviders[i].apply(action);
            ret[i] = new Player(i, bot, moves);
        }
        return ret;
    }

    static GameMap createMap(String file) {
        try {
            Replay replay = OBJECT_MAPPER.readValue(new File(file), Replay.class);
            return GameMap.of(replay);
        } catch (IOException e) {
            throw new RuntimeException("Can not create game map from file: " + file, e);
        }
    }

    static GameMap createTestMap() {
        Tile[] tiles = new Tile[]{
                new General(0, 0), new City(1, 11), new Mountain(2),
                new Empty(3), new Empty(4), new Empty(5),
                new Empty(6), new Empty(7), new General(8, 1)
        };
        return new GameMap(tiles, 3, 3);
    }


}
