package pl.joegreen.sergeants.framework.queue;

import pl.joegreen.sergeants.api.GeneralsApi;

import java.util.Optional;

class CustomGameQueueConfiguration extends QueueConfiguration {
    private final String gameId;
    private final Optional<Integer> optionalTeamId;

    CustomGameQueueConfiguration(boolean force, String gameId) {
        super(force);
        this.gameId = gameId;
        optionalTeamId = Optional.empty();
    }


    CustomGameQueueConfiguration(boolean force, String gameId, int teamId) {
        super(force);
        this.gameId = gameId;
        optionalTeamId = Optional.of(teamId);
    }

    @Override
    public void joinQueue(GeneralsApi api, String botUserId) {
        api.joinCustomGameQueue(botUserId, gameId);
        optionalTeamId.ifPresent(integer -> api.setTeamInCustomGame(gameId, integer));
        if (force) {
            api.forceStartCustomGameQueue(true, gameId);
        }
    }
}
