package pl.joegreen.sergeants.api.listener;

@FunctionalInterface
public interface OneArgListener<T> {
    void onEvent(T event);
}
