package ru.mail.track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.server.NIOServer;
import ru.mail.track.server.Session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Egor on 27.01.2016.
 */
public class NIOConnectionHandler implements ConnectionHandler {
    static Logger log = LoggerFactory.getLogger(NIOConnectionHandler.class);
    private List<MessageListener> listeners = new ArrayList<>();
    private ByteBuffer byteBuffer;
    private Session session;
    private Protocol protocol;
    private ByteBuffer inputBuffer;
    private Selector selector;
    private Map<SocketChannel, ByteBuffer> dataToWrite;
    private SocketChannel socketChannel;
    SelectionKey sKey;
    public NIOConnectionHandler(Session session, Protocol protocol,
                                Map<SocketChannel, ByteBuffer> dataToWrite, SocketChannel socketChannel, Selector selector) {
        this.session = session;
        this.protocol = protocol;
        this.dataToWrite = dataToWrite;
        this.socketChannel = socketChannel;
        this.selector = selector;
    }
    public void setInputBuffer(ByteBuffer inputBuffer) {
        this.inputBuffer = inputBuffer;
    }
    public void setSelectionKey(SelectionKey sKey) {
        this.sKey = sKey;
    }

    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        this.dataToWrite.put(socketChannel, ByteBuffer.wrap(protocol.encode(msg)));
        log.info("Sending message {}", protocol.decode(dataToWrite.get(socketChannel).array()).getType());
        socketChannel.register(selector, SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(Session session, Message msg) {
        listeners.forEach(listener -> listener.onMessage(session, msg));
    }
    @Override
    public void stop() {

    }

    @Override
    public void run() {
        Message msg = null;
        try {
            msg = protocol.decode(inputBuffer.array());
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        log.info("Notify listeners with messageType {}", msg.getType());
        this.notifyListeners(session, msg);
    }
}
