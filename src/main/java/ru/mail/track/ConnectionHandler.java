package ru.mail.track;

import ru.mail.track.protocol.ProtocolException;

import java.io.IOException;

public interface ConnectionHandler extends Runnable {
    void send(Message msg) throws IOException, ProtocolException;

    void addListener(MessageListener listener);

    void stop();

}
