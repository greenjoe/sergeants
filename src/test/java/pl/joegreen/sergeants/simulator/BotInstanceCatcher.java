package pl.joegreen.sergeants.simulator;

import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;

import java.util.function.Function;

class BotInstanceCatcher<T extends Bot> implements Function<Actions, Bot> {

    private final Function<Actions, T> botProvider;
    public T lastCreatedBot;

    public BotInstanceCatcher(Function<Actions, T> botProvider) {
        this.botProvider = botProvider;
    }

    @Override
    public Bot apply(Actions actions) {
        T botInstance = botProvider.apply(actions);
        lastCreatedBot = botInstance;
        return botInstance;
    }
}
