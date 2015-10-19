package ru.mail.track;

import java.io.IOException;

/**
 * Created by egor on 18.10.15.
 */
public class Session {
    private User user;



    private boolean isLogin;
    public UserStorage userStorage;
    public MessageStorage messageStorage;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Session(UserStorage userStorage, MessageStorage messageStorage) {
        this.userStorage = userStorage;
        this.messageStorage = messageStorage;
        this.isLogin = false;
    }
    public boolean getIsLogin() {
        return isLogin;
    }
    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    public void saveData() {
        userStorage.writeToFile();
        messageStorage.writeToFile();
    }
}
