package pl.joegreen.sergeants.framework.model;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface GameState extends GameStateFieldContext {
    /**
     * Horizontal size of the map. Does not change during the game.
     */
    int getColumns();

    /**
     * Vertical size of the map. Does not change during the game.
     */
    int getRows();

    /**
     * "Server turn" which is increased after every update (possibility of making a move). Is not the same as turn number
     * displayed in browser (browser turn is equal to two server turns).
     */
    int getTurn();

    /**
     * Officially "the attackIndex field is unnecessary for bots -- it's used by our client to show/hide attack arrows when appropriate."
     * Some people say it can be used to find out state of moves queue and plan more complex moves ;-)
     */
    int getAttackIndex();

    /**
     * Players currently playing and those already dead. Represents the table that is displayed in browser in the
     * top right corner of the map.
     */
    ImmutableList<Player> getPlayers();

    ImmutableMap<Position, Field> getFieldsMap();

    GameStarted getGameStartedData();

    default Player getMyPlayer() {
        return getPlayers().get(getMyPlayerIndex());
    }

    default ImmutableMap<Position, VisibleField> getVisibleFieldsMap() {
        return ImmutableMap.copyOf(getFieldsMap().values().stream()
                .filter(Field::isVisible)
                .map(Field::asVisibleField)
                .collect(Collectors.toMap(Field::getPosition, Functions.identity())));
    }

    default Collection<Field> getFields() {
        return getFieldsMap().values();
    }

    default Collection<VisibleField> getVisibleFields() {
        return getVisibleFieldsMap().values();
    }

    default Field[][] getTwoDimensionalArrayOfFields() {
        Field[][] array = new Field[getRows()][getColumns()];
        for (int row = 0; row < getRows(); ++row) {
            for (int col = 0; col < getColumns(); ++col) {
                array[row][col] = getFieldsMap().get(new Position(row, col));
            }
        }
        return array;
    }

    default Position positionFromIndex(int index) {
        return new Position(index / getColumns(), index % getColumns());
    }

    default int positionToIndex(Position position) {
        return position.getCol() + position.getRow() * getColumns();
    }

    default boolean isValidPosition(Position position) {
        return position.getCol() < getColumns() && position.getCol() >= 0 && position.getRow() >= 0 && position.getRow() < getRows();
    }

    default Set<Integer> getMyTeamIndexes() {
        return getPlayers().stream().filter(
                player -> player.getTeam().equals(getPlayers().get(getMyPlayerIndex()).getTeam()))
                .mapToInt(Player::getIndex)
                .boxed()
                .collect(Collectors.toSet());
    }


    /**
     * Index of bot's player.
     */
    default int getMyPlayerIndex() {
        return getGameStartedData().getPlayerIndex();
    }

    /**
     * Replay identifier. Replays are available only after game is finished.
     * Use http://bot.generals.io/replays/{replayId} to see the replay in your browser.
     */
    default String getReplayId() {
        return getGameStartedData().getReplayId();
    }

    /**
     * Identifier of the main game chat room.
     */
    default String getChatRoom() {
        return getGameStartedData().getChatRoom();
    }

    /**
     * Identifier of the private team chat room.
     */
    default String getTeamChatRoom() {
        return getGameStartedData().getTeamChatRoom();
    }

    default Set<Position> getNeighbourPositions(Position position) {
        return Stream.of(
                position.withCol(position.getCol() + 1),
                position.withCol(position.getCol() - 1),
                position.withRow(position.getRow() + 1),
                position.withRow(position.getRow() - 1)
        ).filter(this::isValidPosition).collect(Collectors.toSet());
    }

    default Set<Field> getNeighbours(Field field) {
        return getNeighbourPositions(field.getPosition())
                .stream()
                .map(this.getFieldsMap()::get)
                .collect(Collectors.toSet());
    }



}
