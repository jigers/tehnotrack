package ru.mail.track;

import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.server.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by egor on 13.11.15.
 */
public class SocketConnectionHandler implements ConnectionHandler {
    private List<MessageListener> listeners = new ArrayList<>();
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Protocol protocol;
    private Session session;

    public SocketConnectionHandler(Protocol protocol, Session session, Socket socket) throws IOException {
        this.protocol = protocol;
        this.session = session;
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }
    public void setSession(Session session) {
        this.session = session;
    }
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }

    public void notifyListeners(Session session, Message msg) {
        listeners.forEach(it -> it.onMessage(session, msg));
    }

    @Override
    public void run() {
        final byte[] buf = new byte[1024 * 64];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int read = in.read(buf);
                if (read > 0) {
                    Message msg = protocol.decode(Arrays.copyOf(buf, read));
                    notifyListeners(session, msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
