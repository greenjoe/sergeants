package pl.joegreen.sergeants.api;

import java.util.function.Consumer;

/**
 * SocketIO proxy interface that is needed to allow testing with fake sockets.
 */
public interface Socket {
    boolean connected();

    void once(String event, Consumer<Object[]> socketListener);

    void on(String event, Consumer<Object[]> socketListener);

    void connect();

    void disconnect();

    void emit(String name, Object[] args);

}
