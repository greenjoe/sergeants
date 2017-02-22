package pl.joegreen.sergeants.framework.queue;

import pl.joegreen.sergeants.api.GeneralsApi;

class TwoVsTwoQueueConfiguration extends QueueConfiguration {
    private String teamId;

    TwoVsTwoQueueConfiguration(String teamId, boolean force) {
        super(force);
        this.teamId = teamId;
    }

    @Override
    public void joinQueue(GeneralsApi api, String botUserId) {
        api.joinTwoVsTwoQueue(botUserId, teamId);
        if (force) {
            api.forceStartTwoVsTwoQueue(true);
        }
    }
}
