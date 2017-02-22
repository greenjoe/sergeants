package pl.joegreen.sergeants.framework.model;

import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Value
public class GameResult {
    public enum Result {
        WON, LOST, DISCONNECTED
    }

    Result result;

    /**
     * Last game state received before game was finished.
     */
    GameState lastGameState;

    /**
     * Name of the player that killed the bot.
     */
    Optional<String> killer;

    /**
     * Names of all players that played in the game.
     */
    public List<String> getPlayerNames() {
        return lastGameState.getPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
    }

    /**
     * {@link GameState#getReplayId()}
     */
    public String getReplayId() {
        return lastGameState.getReplayId();
    }

    @Override
    public String toString() {
        return String.format("Game result: %s, players: %s, replay: http://bot.generals.io/replays/%s", result, getPlayerNames(), getReplayId())
                + killer.map(name -> ", killer: " + name).orElse("");
    }
}
