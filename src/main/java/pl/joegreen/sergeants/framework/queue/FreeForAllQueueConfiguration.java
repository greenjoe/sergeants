package pl.joegreen.sergeants.framework.queue;

import pl.joegreen.sergeants.api.GeneralsApi;

class FreeForAllQueueConfiguration extends QueueConfiguration {
    FreeForAllQueueConfiguration(boolean force) {
        super(force);
    }

    @Override
    public void joinQueue(GeneralsApi api, String botUserId) {
        api.joinFreeForAllQueue(botUserId);
        if (force) {
            api.forceStartFreeForAllQueue(true);
        }
    }
}
