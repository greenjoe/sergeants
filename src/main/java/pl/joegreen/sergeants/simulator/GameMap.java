package pl.joegreen.sergeants.simulator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GameMap {
    private final Logger LOGGER = LoggerFactory.getLogger(GameMap.class);

    private ObjectMapper om = new ObjectMapper();

    private final Tile[] tiles;
    private int halfTurnCounter = 0;
    private final int width;
    private final int height;
    private List<String> history = new ArrayList<>();


    public GameMap(Tile[] tiles, int height, int width) {
        if ((height * width) != tiles.length) {
            throw new IllegalArgumentException("Incorrect height and width");
        }
        this.height = height;
        this.width = width;
        this.tiles = tiles;
    }

    public GameMap(int height, int width) {
        this(new Tile[height * width], height, width);
    }

    public static GameMap of(final Replay replay) {
        GameMap ret = new GameMap(replay.getMapHeight(), replay.getMapWidth());

        Arrays.stream(replay.getMountains()).forEach(tileId -> ret.tiles[tileId] = new Mountain(tileId));

        IntStream.range(0, replay.getCities().length).forEach(i -> {
            int tileIndex = replay.getCities()[i];
            int armySize = replay.getCityArmies()[i];
            ret.tiles[tileIndex] = new City(tileIndex, armySize);
        });

        IntStream.range(0, replay.getGenerals().length).forEach(playerIndex -> {
            int tileIndex = replay.getGenerals()[playerIndex];
            String username = replay.getUsernames()[playerIndex];
            ret.tiles[tileIndex] = new General(tileIndex, playerIndex);
        });

        IntStream.range(0, ret.tiles.length).forEach(tileIndex -> {
            if (ret.tiles[tileIndex] == null) {
                ret.tiles[tileIndex] = new Empty(tileIndex);
            }
        });

        return ret;
    }

    Tile[] getTiles() {
        return tiles;
    }

    public void tick() {
        halfTurnCounter++;

        if ((halfTurnCounter % 2) == 0) {
            Arrays.stream(tiles).forEach(Tile::turn);
        }
        if ((halfTurnCounter % 25) == 0) {
            Arrays.stream(tiles).forEach(Tile::round);
        }
        try {
            history.add(om.writeValueAsString(tiles));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Write json failed: " + halfTurnCounter, e);
        }
    }

    /**
     * Polls move recursively until it finds a valid move.
     */
    public Optional<PlayerKilled> move(Deque<Move> moves) {
        Move move = moves.poll();
        if (move != null) {
            Tile fromTile = tiles[move.getFrom()];
            int armySize = fromTile.moveFrom(move.half());
            if (armySize > 0) {
                return tiles[move.getTo()].moveTo(armySize, fromTile.getPlayerIndex(), tiles).map(this::transfer);
            } else {
                return move(moves);
            }
        }
        return Optional.empty();
    }

    private PlayerKilled transfer(PlayerKilled playerKilled) {
        Arrays.stream(tiles)
                .filter(tile -> tile.getPlayerIndex() == playerKilled.getVictim())
                .forEach(tile -> tile.transfer(playerKilled.getOffender()));
        return playerKilled;
    }

    public GameUpdateApiResponse getUpdate(int playerIndex) {
        int[] mapDiff = getMapDiff(playerIndex);
        int[] citiesDiff = getCitiesDiff(playerIndex);
        int[] generals = getGenerals(playerIndex);
        ScoreApiResponse[] scores = new ScoreApiResponse[0];
        double[] stars = new double[0];
        return new GameUpdateApiResponse(halfTurnCounter, mapDiff, citiesDiff, generals, 0, scores, stars);
    }

    private int[] getMapDiff(int playerIndex) {
        List<Integer> mapDiff = new ArrayList<>();
        //invalidate whole mapdiff and send a complete update because no need to save bandwidth
        mapDiff.add(0);
        mapDiff.add(tiles.length * 2 + 2);
        mapDiff.add(width);
        mapDiff.add(height);

        for (Tile tile : tiles) {
            int armySize = isVisible(playerIndex, tile) ? tile.getArmySize() : 0;
            mapDiff.add(armySize);
        }

        //todo add fog
        for (Tile tile : tiles) {
            mapDiff.add(tile.getTerrain());
        }
        return mapDiff.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] getCitiesDiff(int playerIndex) {
        //todo add fog
        List<Integer> allCities = Arrays.stream(tiles)
                .filter(tile -> tile.getClass() == City.class)
                .map(Tile::getTileIndex)
                .collect(Collectors.toList());

        allCities.add(0, allCities.size());
        allCities.add(0, 0);
        return allCities.stream().mapToInt(i -> i).toArray();
    }

    private int[] getGenerals(int playerIndex) {
        //todo add fog
        return Arrays.stream(tiles)
                .filter(tile1 -> tile1.getClass().equals(General.class))
                .sorted((t1, t2) -> t1.getPlayerIndex() - t2.getPlayerIndex())
                .mapToInt(Tile::getTileIndex)
                .toArray();
    }

    private boolean isVisible(int playerIndex, Tile tile) {
        boolean selfTile = tile.getPlayerIndex() == playerIndex;
        return selfTile || getSurroundingTiles(tile).stream().anyMatch(t -> t.getPlayerIndex() == playerIndex);
    }

    private List<Tile> getSurroundingTiles(Tile tile) {
        //this could be prettier
        List<Tile> ret = new ArrayList<>();
        getTileLeftOf(tile).ifPresent(leftTile -> {
            ret.add(leftTile);
            getTileAbove(leftTile).ifPresent(ret::add);
            getTileBelow(leftTile).ifPresent(ret::add);
        });
        getTileRightOf(tile).ifPresent(t -> {
            ret.add(t);
            getTileAbove(t).ifPresent(ret::add);
            getTileBelow(t).ifPresent(ret::add);
        });
        getTileAbove(tile).ifPresent(ret::add);
        getTileBelow(tile).ifPresent(ret::add);
        return ret;
    }

    private Optional<Tile> getTileAbove(Tile tile) {
        int tileIndex = tile.getTileIndex() - width;
        return tileIndex >= 0 ? Optional.of(tiles[tileIndex]) : Optional.empty();
    }

    private Optional<Tile> getTileBelow(Tile tile) {
        int tileIndex = tile.getTileIndex() + width;
        return tileIndex < tiles.length ? Optional.of(tiles[tileIndex]) : Optional.empty();
    }

    private Optional<Tile> getTileRightOf(Tile tile) {
        boolean isMostRight = tile.getTileIndex() % width == width - 1;
        return isMostRight ? Optional.empty() : Optional.of(tiles[tile.getTileIndex() + 1]);
    }

    private Optional<Tile> getTileLeftOf(Tile tile) {
        int tileIndex = tile.getTileIndex();
        boolean isMostLeft = tileIndex == 0 || tileIndex % width == 0;
        return isMostLeft ? Optional.empty() : Optional.of(tiles[tileIndex - 1]);
    }

    public int getHalfTurnCounter() {
        return halfTurnCounter;
    }

    public void saveGameAsJson() {
        //pls don't look at this code
        try {
            final File file = new File("replay-data.js");
            final PrintWriter printWriter = new PrintWriter(file);
            printWriter.println("var generalIoReplay = {};");
            printWriter.println("window.generalIoReplay = generalIoReplay;");
            printWriter.println("generalIoReplay.width = " + width + ";");
            printWriter.println("generalIoReplay.height = " + height + ";");
            printWriter.println("generalIoReplay.history = [];");
            history.forEach(s -> printWriter.println("generalIoReplay.history.push(JSON.parse('" + s + "'));"));
            printWriter.flush();
            printWriter.close();
            LOGGER.info("******* A js file with replay has been created at: {}", file.getAbsoluteFile());
        } catch (IOException e) {
            LOGGER.error("Could not write json data history", e);
        }
    }
}