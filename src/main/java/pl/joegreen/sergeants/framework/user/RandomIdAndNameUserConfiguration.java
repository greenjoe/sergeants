package pl.joegreen.sergeants.framework.user;

import java.util.UUID;

class RandomIdAndNameUserConfiguration extends IdAndNameUserConfiguration {

    RandomIdAndNameUserConfiguration() {
        super(generateUserId(), generateRandomUsername());
    }

    private static String generateRandomUsername() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private static String generateUserId() {
        return UUID.randomUUID().toString();
    }
}
