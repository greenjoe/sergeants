package pl.joegreen.sergeants.framework.model;

import pl.joegreen.sergeants.api.response.GameStartApiResponse;
import pl.joegreen.sergeants.api.response.GameUpdateApiResponse;
import pl.joegreen.sergeants.api.response.ScoreApiResponse;
import pl.joegreen.sergeants.api.util.MapPatcher;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ToString
@EqualsAndHashCode
public class GameState {
    private final int columns;
    private final int rows;
    private final ImmutableList<Player> players;
    private final ImmutableMap<Position, Field> fields;
    private final int turn;


    private GameStartApiResponse startData;
    private int[] rawMapArray = null;
    private int[] rawCitiesArray = null;
    private int[] rawGeneralsArray = null;


    /**
     * Creates the first game state just after the game is started. Can be done only after both messages are received from server:
     * {@link GameStartApiResponse} and {@link GameUpdateApiResponse}. Cannot be use for further updates, as later game updates
     * from server contain only differences.
     */
    public static GameState createInitialGameState(GameStartApiResponse startData, GameUpdateApiResponse firstUpdateData) {
        return new GameState(
                firstUpdateData.getTurn(), startData, MapPatcher.patch(firstUpdateData.getMapDiff(), new int[]{}),
                MapPatcher.patch(firstUpdateData.getCitiesDiff(), new int[]{}),
                firstUpdateData.getGenerals(),
                createPlayersInfo(startData, firstUpdateData)
        );
    }


    /**
     * Updates game state from previous turn with new information received from server. The method only works fine if
     * updates are applied in sequence, as server is only sending map differences in update messages.
     */
    public GameState update(GameUpdateApiResponse gameUpdateData) {
        return new GameState(
                gameUpdateData.getTurn(), startData, MapPatcher.patch(gameUpdateData.getMapDiff(), rawMapArray),
                MapPatcher.patch(gameUpdateData.getCitiesDiff(), rawCitiesArray),
                gameUpdateData.getGenerals(),
                createPlayersInfo(startData, gameUpdateData)
        );
    }


    /**
     * Horizontal size of the map. Does not change during the game.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Vertical size of the map. Does not change during the game.
     */
    public int getRows() {
        return rows;
    }


    /**
     * "Server turn" which is increased after every update (possibility of making a move). Is not the same as turn number
     * displayed in browser (browser turn is equal to two server turns).
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Players currently playing and those already dead. Represents the table that is displayed in browser in the
     * top right corner of the map.
     */
    public ImmutableList<Player> getPlayers() {
        return ImmutableList.copyOf(players);
    }

    /**
     * Index of bot's player.
     */
    public int getMyPlayerIndex() {
        return startData.getPlayerIndex();
    }

    /**
     * Representation of bot's player.  {@see GameState#getPlayers()}
     */
    public Player getMyPlayer() {
        return players.get(getMyPlayerIndex());
    }

    /**
     *  Replay identifier. Replays are available only after game is finished.
     *  Use http://bot.generals.io/replays/{replayId} to see the replay in your browser.
     */
    public String getReplayId() {
        return startData.getReplayId();
    }

    /**
     * Identifier of the main game chat room.
     */
    public String getChatRoom() {
        return startData.getChatRoom();
    }

    /**
     * Identifier of the private team chat room.
     */
    public String getTeamChatRoom() {
        return startData.getTeamChatRoom();
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


    private GameState(int turn, GameStartApiResponse startData, int[] fields, int[] cities, int[] generals, ImmutableList<Player> players) {
        this.turn = turn;
        this.rawCitiesArray = cities;
        this.rawMapArray = fields;
        this.rawGeneralsArray = generals;
        this.players = players;
        this.startData = startData;
        this.columns = rawMapArray[0];
        this.rows = rawMapArray[1];
        this.fields = createFieldsMapFromApiArrays();
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
            return new Field(position, terrainType);
        } else {
            return new VisibleField(position, terrainType, ownerPlayerIndex, army,
                    Arrays.stream(rawCitiesArray).anyMatch(cityIndex -> cityIndex == positionToIndex(position)),
                    Arrays.stream(rawGeneralsArray).anyMatch(generalIndex -> generalIndex == positionToIndex(position))

            );
        }
    }


    public ImmutableMap<Position, VisibleField> getVisibleFieldsMap() {
        return ImmutableMap.copyOf(fields.values().stream()
                .filter(Field::isVisible)
                .map(Field::asVisibleField)
                .collect(Collectors.toMap(Field::getPosition, Functions.identity())));
    }


    public ImmutableMap<Position, Field> getFieldsMap() {
        return fields;
    }

    public Collection<Field> getFields() {
        return fields.values();
    }

    public Collection<VisibleField> getVisibleFields() {
        return getVisibleFieldsMap().values();
    }

    public Field[][] getTwoDimensionalArrayOfFields() {
        Field[][] array = new Field[rows][columns];
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                array[row][col] = fields.get(new Position(row, col));
            }
        }
        return array;
    }


    public Position positionFromIndex(int index) {
        return new Position(index / columns, index % columns);
    }

    public int positionToIndex(Position position) {
        return position.getCol() + position.getRow() * columns;
    }

    /**
     * Checks if given position lays inside the map.
     */
    public boolean isValidPosition(Position position) {
        return position.getCol() < columns && position.getCol() >= 0 && position.getRow() >= 0 && position.getRow() < rows;
    }


    /**
     * Indexes of players being in the same team as bot.
     */
    public Collection<Integer> getMyTeamIndexes() {
        return players.stream().filter(player -> player.getTeam().equals(players.get(getMyPlayerIndex()).getTeam())).mapToInt(Player::getIndex)
                .boxed().collect(Collectors.toCollection(ArrayList::new));
    }

    @ToString(callSuper = true)
    public class Field {


        public Position getPosition() {
            return position;
        }


        private FieldTerrainType getTerrainType() {
            return terrainType;
        }

        protected Position position;
        protected FieldTerrainType terrainType;

        Field(Position position, FieldTerrainType terrainType) {
            this.position = position;
            this.terrainType = terrainType;
        }

        /**
         * Determines whether in the current round the field is visible or in the fog of war.
         * If this method returns true, {@link #asVisibleField()} method can be used to obtain additional info about this field.
         *
         * @return true if all details of this field are known, false if the field is behind the fog of war (too far to be seen)
         */
        public boolean isVisible() {
            return false;
        }

        /**
         * If the field is not visible, true indicates city or mountain, if the field is visible true indicates only mountain.
         */
        public boolean isObstacle() {
            return terrainType.equals(FieldTerrainType.FOG_OBSTACLE);
        }

        /**
         * Converts to a VisibleField instance that exposes more details about the field. Throws an exception
         * if the field is not visible, so it should be checked before calling this method with {@link Field#isVisible}.
         */
        public VisibleField asVisibleField() {
            throw new IllegalStateException("Field is not visible");
        }

        /**
         * Returns fields that can be reached from this field in one move.
         * Those fields are the field directly above, below, on the right and on the left. Method will ommit
         * fields not being inside the map, so it handles borders well.
         */
        public Set<Field> getNeighbours() {
            return Stream.of(
                    position.withCol(position.getCol() + 1),
                    position.withCol(position.getCol() - 1),
                    position.withRow(position.getRow() + 1),
                    position.withRow(position.getRow() - 1)
            ).filter(GameState.this::isValidPosition)
                    .map(fields::get).collect(Collectors.toSet());
        }

        /**
         * Same as {@link Field#getNeighbours()} but filtered with {@link Field#isVisible()}.
         */
        public Set<VisibleField> getVisibleNeighbours() {
            return getNeighbours().stream()
                    .filter(Field::isVisible)
                    .map(Field::asVisibleField)
                    .collect(Collectors.toSet());
        }

        public int getIndex() {
            return positionToIndex(position);
        }

    }

    @ToString(callSuper = true)
    public class VisibleField extends Field {
        private int army;
        private boolean city;
        private boolean general;
        private Optional<Integer> ownerIndex;


        VisibleField(Position position, FieldTerrainType terrainType, Optional<Integer> ownerIndex, int army, boolean city, boolean general) {
            super(position, terrainType);
            this.ownerIndex = ownerIndex;
            this.army = army;
            this.city = city;
            this.general = general;
        }

        @Override
        public boolean isVisible() {
            return true;
        }

        @Override
        public boolean isObstacle() {
            return terrainType.equals(FieldTerrainType.MOUNTAIN);
        }

        @Override
        public VisibleField asVisibleField() {
            return this;
        }

        /**
         * Index of player that owns the field.
         */
        public Optional<Integer> getOwnerIndex() {
            return ownerIndex;
        }

        public boolean isOwnedBy(int playerIndex){
            return Optional.of(playerIndex).equals(ownerIndex);
        }

        public boolean isOwnedByMe() {
            return isOwnedBy(getMyPlayerIndex());
        }

        public boolean isOwnedByMyTeam() {
            return ownerIndex.isPresent() && getMyTeamIndexes().contains(ownerIndex.get());
        }

        /**
         * Returns true if field has an owner but owner is not in the same team as bot.
         */
        public boolean isOwnedByEnemy(){
            return hasOwner() && !isOwnedByMyTeam();
        }


        /**
         * Returns true if field is represented with a white square in the brower (not mountain, not city, no owner).
         */
        public boolean isBlank(){
            return !hasOwner() && !isObstacle() && !isCity();
        }

        public boolean hasOwner() {
            return ownerIndex.isPresent();
        }

        public int getArmy() {
            return army;
        }

        public boolean isGeneral() {
            return general;
        }
        public boolean isCity() {
            return city;
        }

    }


}
