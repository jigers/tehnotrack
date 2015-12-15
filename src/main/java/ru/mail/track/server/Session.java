package ru.mail.track.server;

import ru.mail.track.ConnectionHandler;

/**
 * Created by egor on 18.10.15.
 */
public class Session {
    private User user;
    private boolean isLogin;
    private long id;

    public UserStorage userStorage;
    public SessionManager sessionManager;
    public ChatStorage chatStorage;
    public MessageStorage messageStorage;
    private ConnectionHandler connectionHandler;

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Session(long id, UserStorage userStorage,
                   ChatStorage chatStorage, SessionManager sessionManager, ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.sessionManager = sessionManager;
        this.userStorage = userStorage;
        this.chatStorage = chatStorage;
        this.id = id;
    }

    public Session(long id, UserStorage userStorage, MessageStorage messageStorage,
                   ChatStorage chatStorage, SessionManager sessionManager, ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        this.sessionManager = sessionManager;
        this.userStorage = userStorage;
        this.chatStorage = chatStorage;
        this.id = id;
    }
    public void setConnectionHandler (ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }
    public Session(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public boolean getIsLogin() {
        return isLogin;
    }
    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
