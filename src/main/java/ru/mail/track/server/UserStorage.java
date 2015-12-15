package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;

/**
 * Created by egor on 12.11.15.
 */
public interface UserStorage {
    void init(PGPoolingDataSource source) throws Exception;

    void close() throws Exception;

    boolean isUserExist(String name);

    void addUser(String name, String pass);

    User getUser(String name);

    User getUser(int id);

    void updateNickname(int userId, String nickname);

    void changePass(int userId, String newPass);

}
