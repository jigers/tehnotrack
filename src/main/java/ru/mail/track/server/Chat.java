package ru.mail.track.server;

import java.util.List;

/**
 * Created by egor on 24.11.15.
 */
public class Chat {
    private List<User> users;
    private User leader;
    private int id;

    public void addUser(User user) {
        this.users.add(user);
    }

    public List<User> getUsers() {
        return this.users;
    }

    public User getLeader() {
        return this.leader;
    }

    public int getId() {
        return this.id;
    }

    public Chat(List<User> users, User leader, int id) {
        this.users = users;
        this.leader = leader;
        this.id = id;
    }

    public boolean contain(int userId) {
        for (User user: users) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }



}
