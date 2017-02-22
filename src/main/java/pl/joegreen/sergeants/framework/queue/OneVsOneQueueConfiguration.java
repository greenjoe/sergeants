package pl.joegreen.sergeants.framework.queue;

import pl.joegreen.sergeants.api.GeneralsApi;

class OneVsOneQueueConfiguration extends QueueConfiguration {
    OneVsOneQueueConfiguration() {
        super(false); /*No need to force 1v1*/
    }

    @Override
    public void joinQueue(GeneralsApi api, String botUserId) {
        api.joinOneVsOneQueue(botUserId);
        if (force) {
            api.forceStartOneVsOneQueue(true);
        }
    }
}
