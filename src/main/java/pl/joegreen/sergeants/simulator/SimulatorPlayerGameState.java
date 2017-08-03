package pl.joegreen.sergeants.simulator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import pl.joegreen.sergeants.framework.model.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

class SimulatorPlayerGameState implements GameState {
    private static final String SIMULATOR_PLACEHOLDER = "GAME_SIMULATION";
    private int width;
    private int height;
    private int halfTurnCounter;
    private Player[] players;
    private final Map<Integer, Simulator.PlayerStats> playersStats;

    private int playerIndex;
    private ImmutableMap<Position, Field> fields;


    SimulatorPlayerGameState(int width, int height, int halfTurnCounter, Player[] players, int playerIndex, IdentityHashMap<Tile, Boolean> tilesToVisibility, Map<Integer, Simulator.PlayerStats> playersStats) {
        this.width = width;
        this.height = height;
        this.halfTurnCounter = halfTurnCounter;
        this.players = players;
        this.playerIndex = playerIndex;
        this.playersStats = playersStats;
        Map<Position, Field> fields = new HashMap<>();
        for (Tile tile : tilesToVisibility.keySet()) {
            Position position = Position.fromIndex(tile.getTileIndex(), width);
            Field field = tileToField(position, tile, this, tilesToVisibility.get(tile));
            fields.put(position, field);
        }
        this.fields = ImmutableMap.copyOf(fields);
    }


    @Override
    public int getColumns() {
        return width;
    }

    @Override
    public int getRows() {
        return height;
    }

    @Override
    public int getTurn() {
        return halfTurnCounter;
    }

    @Override
    public int getAttackIndex() {
        return 0;
    }

    @Override
    public ImmutableList<pl.joegreen.sergeants.framework.model.Player> getPlayers() {
        return ImmutableList.copyOf(Arrays.stream(players)
                .map(player -> {
                    int index = player.getPlayerIndex();
                    Simulator.PlayerStats stats = playersStats.getOrDefault(index, new Simulator.PlayerStats(0, 0));
                    return new pl.joegreen.sergeants.framework.model.Player(index, player.getName(), player.getPlayerIndex(), stats.tiles, stats.army, false);
                })
                .collect(Collectors.toList()));
    }

    @Override
    public ImmutableMap<Position, Field> getFieldsMap() {
        return fields;
    }

    @Override
    public GameStarted getGameStartedData() {
        return new GameStarted() {
            @Override
            public String getReplayId() {
                return SIMULATOR_PLACEHOLDER;
            }

            @Override
            public String[] getUsernames() {
                return Arrays.stream(players).map(Player::getName).toArray(String[]::new);
            }

            @Override
            public int getPlayerIndex() {
                return playerIndex;
            }

            @Override
            public String getChatRoom() {
                return SIMULATOR_PLACEHOLDER;
            }

            @Override
            public String getTeamChatRoom() {
                return SIMULATOR_PLACEHOLDER;
            }
        };
    }

    private Field tileToField(Position position, Tile tile, GameStateFieldContext fieldContext, boolean visible) {
        if (visible) {
            return new VisibleField(fieldContext, position, convertTerrainType(tile.getTerrainType(true)), tile.getOwnerPlayerIndex(), tile.getArmySize(), tile instanceof CityTile, tile instanceof GeneralTile);
        } else {
            return new Field(fieldContext, position, convertTerrainType(tile.getTerrainType(false)));
        }
    }

    private FieldTerrainType convertTerrainType(TerrainType type) {
        if (type == TerrainType.TILE_EMPTY) {
            return FieldTerrainType.EMPTY;
        } else if (type == TerrainType.TILE_FOG) {
            return FieldTerrainType.FOG;
        } else if (type == TerrainType.TILE_FOG_OBSTACLE) {
            return FieldTerrainType.FOG_OBSTACLE;
        } else if (type == TerrainType.TILE_MOUNTAIN) {
            return FieldTerrainType.MOUNTAIN;
        } else {
            return FieldTerrainType.OWNED;
        }
    }
}
