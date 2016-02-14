package ru.mail.track.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Message;
import ru.mail.track.MessageListener;
import ru.mail.track.server.Session;

import java.util.*;

public class ClientOutputListener implements MessageListener {
    public List<Message> messageList = new ArrayList<>();
    static Logger log = LoggerFactory.getLogger(ClientOutputListener.class);
    boolean verbose = false;
    @Override
    public void onMessage(Session session, Message message) {
        if (verbose) {
            log.info("Client output: {}", message.getMessage());
        }
        messageList.add(message);
    }

    public boolean empty() {
        return messageList.size() == 0;
    }
}
