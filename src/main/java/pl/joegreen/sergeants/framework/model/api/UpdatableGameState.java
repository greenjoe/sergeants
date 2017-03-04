package pl.joegreen.sergeants.framework.model.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;
import pl.joegreen.sergeants.api.util.MapPatcher;
import pl.joegreen.sergeants.framework.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString
@EqualsAndHashCode
public class UpdatableGameState implements GameState {
    /*Actual data returned to clients*/
    private final int columns;
    private final int rows;
    private final ImmutableList<Player> players;
    private final ImmutableMap<Position, Field> fieldsMap;
    /*caching visibleFieldsMap even though GameState calculates it by default as this map is very likely to be used intensively */
    private final ImmutableMap<Position, VisibleField> visibleFieldsMap;
    private final int turn;
    private final int attackIndex;


    /*Data persisted so that it's possible to update the game state */
    private final GameStartApiResponse startData;
    private final int[] rawMapArray;
    private final int[] rawCitiesArray;
    private final int[] rawGeneralsArray;


    /**
     * Creates the first game state just after the game is started. Can be done only after both messages are received from server:
     * {@link GameStartApiResponse} and {@link GameUpdateApiResponse}. Cannot be use for further updates, as later game updates
     * from server contain only differences.
     */
    public static UpdatableGameState createInitialGameState(GameStartApiResponse startData, GameUpdateApiResponse firstUpdateData) {
        return new UpdatableGameState(
                firstUpdateData.getTurn(), startData, MapPatcher.patch(firstUpdateData.getMapDiff(), new int[]{}),
                MapPatcher.patch(firstUpdateData.getCitiesDiff(), new int[]{}),
                firstUpdateData.getGenerals(),
                createPlayersInfo(startData, firstUpdateData),
                firstUpdateData.getAttackIndex());
    }


    /**
     * Updates game state from previous turn with new information received from server. The method only works fine if
     * updates are applied in sequence, as server is only sending map differences in update messages.
     */
    public UpdatableGameState update(GameUpdateApiResponse gameUpdateData) {
        return new UpdatableGameState(
                gameUpdateData.getTurn(), startData, MapPatcher.patch(gameUpdateData.getMapDiff(), rawMapArray),
                MapPatcher.patch(gameUpdateData.getCitiesDiff(), rawCitiesArray),
                gameUpdateData.getGenerals(),
                createPlayersInfo(startData, gameUpdateData),
                gameUpdateData.getAttackIndex());
    }


    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public int getRows() {
        return rows;
    }


    @Override
    public int getTurn() {
        return turn;
    }

    @Override
    public int getAttackIndex() {
        return attackIndex;
    }

    @Override
    public ImmutableMap<Position, Field> getFieldsMap() {
        return fieldsMap;
    }

    @Override
    public ImmutableMap<Position, VisibleField> getVisibleFieldsMap() {
        return visibleFieldsMap;
    }

    @Override
    public ImmutableList<Player> getPlayers() {
        return players;
    }

    @Override
    public GameStarted getGameStartedData() {
        return new GameStartedApiResponseImpl(startData);
    }


    private static ImmutableList<Player> createPlayersInfo(GameStartApiResponse startData, GameUpdateApiResponse firstUpdateData) {
        int numberOfPlayers = startData.getUsernames().length;
        Map<Integer, ScoreApiResponse> scoresMap = createScoresMap(firstUpdateData);
        List<Player> players = IntStream.range(0, numberOfPlayers).mapToObj(playerIndex -> {
            Optional<ScoreApiResponse> scoreForPlayer = Optional.ofNullable(scoresMap.get(playerIndex));
            return new Player(playerIndex,
                    startData.getUsernames()[playerIndex],
                    Optional.ofNullable(startData.getTeams()).map(teams -> teams[playerIndex]).orElse(playerIndex),
                    scoreForPlayer.map(ScoreApiResponse::getTiles).orElse(null),
                    scoreForPlayer.map(ScoreApiResponse::getTotal).orElse(null),
                    scoreForPlayer.map(ScoreApiResponse::isDead).orElse(false));
        }).collect(Collectors.toList());
        return ImmutableList.copyOf(players);
    }

    private static Map<Integer, ScoreApiResponse> createScoresMap(GameUpdateApiResponse firstUpdateData) {
        return Arrays.stream(firstUpdateData.getScores()).collect(Collectors.toMap(ScoreApiResponse::getIndex, Function.identity()));
    }


    private UpdatableGameState(int turn, GameStartApiResponse startData, int[] fields, int[] cities, int[] generals, ImmutableList<Player> players, int attackIndex) {
        this.turn = turn;
        this.rawCitiesArray = cities;
        this.rawMapArray = fields;
        this.rawGeneralsArray = generals;
        this.players = players;
        this.startData = startData;
        this.attackIndex = attackIndex;
        this.columns = rawMapArray[0];
        this.rows = rawMapArray[1];
        this.fieldsMap = createFieldsMapFromApiArrays();
        this.visibleFieldsMap = GameState.super.getVisibleFieldsMap();
    }


    private ImmutableMap<Position, Field> createFieldsMapFromApiArrays() {
        int size = columns * rows;
        int[] armies = new int[size];
        int[] terrains = new int[size];

        for (int i = 0; i < size; ++i) {
            armies[i] = rawMapArray[i + 2];
            terrains[i] = rawMapArray[size + 2 + i];
        }
        ImmutableMap.Builder<Position, Field> fieldsMapBuilder = new ImmutableMap.Builder<>();
        for (int i = 0; i < size; ++i) {
            Field field = createField(i, armies[i], terrains[i]);
            fieldsMapBuilder.put(field.getPosition(), field);

        }
        return fieldsMapBuilder.build();
    }

    private Field createField(int index, int army, int terrain) {
        Position position = positionFromIndex(index);
        FieldTerrainType terrainType = FieldTerrainType.NUMBER_TO_TERRAIN_TYPE.getOrDefault(terrain, FieldTerrainType.OWNED);
        Optional<Integer> ownerPlayerIndex = terrainType.equals(FieldTerrainType.OWNED) ? Optional.of(terrain) : Optional.empty();
        if (terrainType.equals(FieldTerrainType.FOG) || terrainType.equals(FieldTerrainType.FOG_OBSTACLE)) {
            return new Field(this, position, terrainType);
        } else {
            return new VisibleField(this, position, terrainType, ownerPlayerIndex, army,
                    Arrays.stream(rawCitiesArray).anyMatch(cityIndex -> cityIndex == positionToIndex(position)),
                    Arrays.stream(rawGeneralsArray).anyMatch(generalIndex -> generalIndex == positionToIndex(position))

            );
        }
    }


}
