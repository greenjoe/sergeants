package pl.joegreen.sergeants.simulator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Factory class for creating simulations
 */
public class SimulatorFactory {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Simulator of(GameMap gameMap, Function<Actions, Bot>... botProviders) {
        return of(gameMap, Integer.MAX_VALUE, botProviders);
    }

    @SafeVarargs
    public static Simulator of(GameMap gameMap, int maxTurns, Function<Actions, Bot>... botProviders) {
        if (botProviders.length < 2) {
            throw new IllegalArgumentException("Cannot simulate the game with less than 2 bots");
        }
        return new Simulator(gameMap, createPlayers(botProviders), maxTurns);
    }

    private SimulatorFactory() {
        //only static methods in this class
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


    /**
     * GIOReplay files can be downloaded from http://dev.generals.io/replays
     *
     * @param gioReplayFileLocation file location
     * @return a game map
     */
    public static GameMap createMapFromReplayFile(String gioReplayFileLocation) {
        try {
            Replay replay = OBJECT_MAPPER.readValue(new File(gioReplayFileLocation), Replay.class);
            return createMapFromReplay(replay);
        } catch (IOException e) {
            throw new RuntimeException("Can not create game map from file: " + gioReplayFileLocation, e);
        }
    }

    private static GameMap createMapFromReplay(Replay replay) {

        Tile[] tiles = new Tile[replay.getMapHeight() * replay.getMapWidth()];

        Arrays.stream(replay.getMountains()).forEach(tileId -> tiles[tileId] = new MountainTile(tileId));

        IntStream.range(0, replay.getCities().length).forEach(i -> {
            int tileIndex = replay.getCities()[i];
            int armySize = replay.getCityArmies()[i];
            tiles[tileIndex] = new CityTile(tileIndex, armySize);
        });

        IntStream.range(0, replay.getGenerals().length).forEach(playerIndex -> {
            int tileIndex = replay.getGenerals()[playerIndex];
            tiles[tileIndex] = new GeneralTile(tileIndex, playerIndex);
        });

        IntStream.range(0, tiles.length).forEach(tileIndex -> {
            if (tiles[tileIndex] == null) {
                tiles[tileIndex] = new EmptyTile(tileIndex);
            }
        });
        return new GameMap(tiles, replay.getMapHeight(), replay.getMapWidth());

    }

    public static GameMap create2PlayerMap() {
        // http://generals.io/replays/B5A3EuoLe
        try {
            InputStream in = SimulatorFactory.class.getResourceAsStream("/gioreplay2.json");
            Replay replay = OBJECT_MAPPER.readValue(in, Replay.class);
            in.close();
            return createMapFromReplay(replay);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create replay");
        }
    }

    public static GameMap create8PlayerMap() {
        // http://generals.io/replays/B5_2c4gPe
        try {
            InputStream in = SimulatorFactory.class.getResourceAsStream("/gioreplay8.json");
            Replay replay = OBJECT_MAPPER.readValue(in, Replay.class);
            in.close();
            return createMapFromReplay(replay);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create replay");
        }
    }


}
