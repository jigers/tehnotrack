package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egor on 24.11.15.
 */
@Deprecated
public class MapUserStorage implements UserStorage {
    Map<Integer, User> users= new HashMap<>();
    volatile int userIndex = 0;

    @Override
    public void init(PGPoolingDataSource source) throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean isUserExist(String name) {
        for (Map.Entry<Integer, User> entry: users.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addUser(String name, String pass) {
        int id = ++userIndex;
        users.put(id, new User(id, name, pass));
    }

    @Override
    public User getUser(String name) {
        for (Map.Entry<Integer, User> entry: users.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public void updateNickname(int userId, String nickname) {

    }

    @Override
    public void changePass(int userId, String newPass) {

    }
}
