package ru.mail.track.server;

import ru.mail.track.ConnectionHandler;
import ru.mail.track.server.Session;
import ru.mail.track.server.ChatStorage;
import ru.mail.track.server.MessageStorage;
import ru.mail.track.server.UserStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by egor on 12.11.15.
 */
public class SessionManager {

    private Map<Integer, Session> userSession;
    private AtomicInteger sessionCounter = new AtomicInteger(0);

    public SessionManager() {
        userSession = new HashMap<>();
    }

    public Session createSession(UserStorage userStorage, ChatStorage chatStorage,
                                 ConnectionHandler connectionHandler) {
        Integer id = sessionCounter.getAndIncrement();
        Session session = new Session(id, userStorage, chatStorage, this, connectionHandler);
        return session;
    }

    public Session createSession(UserStorage userStorage, MessageStorage messageStorage, ChatStorage chatStorage,
                                 ConnectionHandler connectionHandler) {
        Integer id = sessionCounter.getAndIncrement();
        Session session = new Session(id, userStorage, messageStorage, chatStorage, this, connectionHandler);
        return session;
    }

    public void forgetUser(int userId) {
        if (this.userConnected(userId)) {
            userSession.remove(userId);
        }
    }

    public void registerUser(int userId, Session session) {
        userSession.put(userId, session);
    }

    public boolean userConnected(int userId) {
        return userSession.containsKey(userId);
    }

    public Session getSessionByUser(int userId) {
        return userSession.get(userId);
    }
}
