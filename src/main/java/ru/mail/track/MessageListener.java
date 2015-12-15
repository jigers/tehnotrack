package ru.mail.track;

import ru.mail.track.server.Session;

/**
 * Created by egor on 12.11.15.
 */
public interface MessageListener {
    void onMessage(Session session, Message message);
}
