package pl.joegreen.sergeants.api.test;

import pl.joegreen.sergeants.api.Socket;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class that can simulate the a for testing purposes.
 */
public class FakeSocket implements Socket{

    public final List<Event> receivedEvents = new CopyOnWriteArrayList<>();
    public final Map<String, Consumer<Object[]>> registeredListeners = new ConcurrentHashMap<>();
    boolean connected = false;

    public List<Event> getReceivedEventsByName(String name) {
        return receivedEvents.stream().filter(event -> event.name.equals(name)).collect(Collectors.toList());
    }

    public void injectEvent(Event event){
        getListener(event.getName()).accept(event.getArguments());
    }

    @Override
    public boolean connected() {
        return connected;
    }

    @Override
    public void once(String event, Consumer<Object[]> socketListener) {
        registeredListeners.put(event, args -> {
            socketListener.accept(args);
            registeredListeners.remove(event);
        });
    }

    @Override
    public void on(String event, Consumer<Object[]> socketListener) {
        registeredListeners.put(event, socketListener);
    }

    @Override
    public void connect() {
        connected = true;
        getListener("connect").accept(new Object[]{});
    }

    @Override
    public void disconnect() {
        connected = false;
        getListener("disconnect").accept(new Object[]{});
    }


    private Consumer<Object[]> getListener(String connect) {
        return registeredListeners.getOrDefault(connect, (args) -> {});
    }


    @Override
    public void emit(String name, Object[] args) {
        receivedEvents.add(new Event(name, args));
    }


    @Value
    public static class Event{
        private String name;
        private Object[] arguments;

        public Event(String name, Object... arguments ){
            this.name = name;
            this.arguments = arguments;
        }
    }



}
