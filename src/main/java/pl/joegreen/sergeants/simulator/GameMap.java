package pl.joegreen.sergeants.simulator;


import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;

import java.util.*;
import java.util.stream.Collectors;

public class GameMap {

    private final Tile[] tiles;
    private int halfTurnCounter = 0;
    private final int width;
    private final int height;


    GameMap(Tile[] tiles, int height, int width) {
        if ((height * width) != tiles.length) {
            throw new IllegalArgumentException("Incorrect height and width");
        }
        this.height = height;
        this.width = width;
        this.tiles = tiles;
    }

    Tile[] getTiles() {
        return tiles;
    }

    void tick() {
        halfTurnCounter++;

        if ((halfTurnCounter % 2) == 0) {
            Arrays.stream(tiles).forEach(Tile::turn);
        }
        if ((halfTurnCounter % 25) == 0) {
            Arrays.stream(tiles).forEach(Tile::round);
        }
    }

    /**
     * Polls move recursively until it finds a valid move.
     */
    Optional<PlayerKilled> move(Player player) {
        Move move = player.getMoves().poll();
        if (move != null) {
            Tile from = tiles[move.getFrom()];
            boolean armyBigEnough = from.getArmySize() > 1;
            boolean tileAndPlayerMatching = from.isOwnedBy(player.getPlayerIndex());
            if (armyBigEnough && tileAndPlayerMatching) {
                int armySize = from.moveFrom(move.half());
                return tiles[move.getTo()]
                        .moveTo(armySize, from.getOwnerPlayerIndex().get(), tiles)
                        .map(this::transfer);
            } else {
                return move(player);
            }
        }
        return Optional.empty();
    }

    private PlayerKilled transfer(PlayerKilled playerKilled) {
        Arrays.stream(tiles)
                .filter(tile -> tile.isOwnedBy(playerKilled.getVictim()))
                .forEach(tile -> tile.transfer(playerKilled.getOffender()));
        return playerKilled;
    }

    GameUpdateApiResponse getUpdate(int playerIndex) {
        int[] mapDiff = getMapDiff(playerIndex);
        int[] citiesDiff = getCitiesDiff();
        int[] generals = getGenerals();
        ScoreApiResponse[] scores = new ScoreApiResponse[0];
        double[] stars = new double[0];
        return new GameUpdateApiResponse(halfTurnCounter, mapDiff, citiesDiff, generals, 0, scores, stars);
    }

    private int[] getMapDiff(int playerIndex) {
        List<Integer> mapDiff = new ArrayList<>();
        //invalidate whole map diff and send a complete update because no need to save bandwidth
        mapDiff.add(0);//this means client has zero correct values
        mapDiff.add(tiles.length * 2 + 2); //this is number of corrected values
        mapDiff.add(width);
        mapDiff.add(height);

        //army size
        for (Tile tile : tiles) {
            int armySize = isVisible(playerIndex, tile) ? tile.getArmySize() : 0;
            mapDiff.add(armySize);
        }

        //terrain
        for (Tile tile : tiles) {
            boolean visible = isVisible(playerIndex, tile);
            TerrainType terrain = tile.getTerrainType(visible);
            mapDiff.add(terrain.intValue);
        }
        return mapDiff.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Returns all cities, visible and fogged. This is not a problem since bot will still receive tile as fogged obstacle (-4).
     *
     * @return list of tileIndex where a city is located
     */
    private int[] getCitiesDiff() {
        List<Integer> allCities = Arrays.stream(tiles)
                .filter(tile -> tile.getClass() == CityTile.class)
                .map(Tile::getTileIndex)
                .collect(Collectors.toList());

        allCities.add(0, allCities.size()); //second element = wrong
        allCities.add(0, 0);//first element = zero correct
        return allCities.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Returns all generals, visible and fogged. This is not a problem since bot will still receive tile as fogged (-3).
     *
     * @return list of tileIndex where general is, array index is player index
     */
    int[] getGenerals() {
        return Arrays.stream(tiles)
                .filter(tile1 -> tile1.getClass().equals(GeneralTile.class))
                .sorted(Comparator.comparingInt(t -> t.getOwnerPlayerIndex().get()))
                .mapToInt(Tile::getTileIndex)
                .toArray();
    }

    private boolean isVisible(int playerIndex, Tile tile) {
        return tile.isOwnedBy(playerIndex) || getSurroundingTiles(tile).stream().anyMatch(t -> t.isOwnedBy(playerIndex));
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

    int getHalfTurnCounter() {
        return halfTurnCounter;
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }
}