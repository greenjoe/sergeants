package pl.joegreen.sergeants.framework.user;

import pl.joegreen.sergeants.api.GeneralsApi;

class IdAndNameUserConfiguration extends UserConfiguration {

    private static final String BOT_NAME_PREFIX = "[Bot]";
    private String name;

    IdAndNameUserConfiguration(String id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public void configureUsername(GeneralsApi api) {
        api.setUsername(getUserId(), BOT_NAME_PREFIX + name);
    }
}
