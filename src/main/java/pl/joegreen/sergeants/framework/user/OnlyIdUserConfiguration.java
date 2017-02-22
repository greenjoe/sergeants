package pl.joegreen.sergeants.framework.user;

import pl.joegreen.sergeants.api.GeneralsApi;

class OnlyIdUserConfiguration extends UserConfiguration {
    OnlyIdUserConfiguration(String userId) {
        super(userId);
    }

    @Override
    public void configureUsername(GeneralsApi api) {
        //do nothing
    }
}
