package pl.joegreen.sergeants.api;

import io.socket.client.IO;

import java.net.URISyntaxException;
import java.util.function.Consumer;

/**
 * Socket implementation that connects to the real SocketIO GeneralsIO socket.
 */
public class RealSocket implements Socket {
    private static final String GENERALSIO_API_URL = "http://botws.generals.io/";


    private final io.socket.client.Socket socket;
    public RealSocket() {
        try {
            this.socket =  IO.socket(GENERALSIO_API_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e); // hardly possible, not enough to have an unchecked exception
        }
    }

    @Override
    public boolean connected() {
        return socket.connected();
    }

    @Override
    public void once(String event, Consumer<Object[]> socketListener) {
        socket.once(event, socketListener::accept);
    }

    @Override
    public void on(String event, Consumer<Object[]> socketListener) {
        socket.on(event, socketListener::accept);
    }


    @Override
    public void connect() {
        socket.connect();

    }

    @Override
    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public void emit(String name, Object[] args) {
        socket.emit(name, args);
    }
}
