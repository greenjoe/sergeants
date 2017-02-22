package pl.joegreen.sergeants.framework.user;

import pl.joegreen.sergeants.api.GeneralsApi;

public abstract class UserConfiguration {

    private String userId;

    protected UserConfiguration(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    abstract public void configureUsername(GeneralsApi api);


    /**
     * Use when name for that ID is already set or you want to remain anonymous.
     * This configuration will not try to set user name.
     */
    public static UserConfiguration onlyUserId(String id) {
        return new OnlyIdUserConfiguration(id);
    }

    /**
     * Use when you want to set the name for the given ID. [Bot] prefix will be added automatically.
     * This configuration will try to set user name. Errors will be ignored, therefore you can keep using this configuration even if the name is already set (it will do nothing).
     */
    public static UserConfiguration idAndName(String id, String name) {
        return new IdAndNameUserConfiguration(id, name);
    }

    /**
     * Equal to {@link UserConfiguration#idAndName(String, String)} but creates a random ID and name.
     * If you want to know the id, call {@link UserConfiguration#getUserId()} on the return value.
     */
    public static UserConfiguration random() {
        return new RandomIdAndNameUserConfiguration();
    }
}
