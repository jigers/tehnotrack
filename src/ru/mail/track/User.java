package ru.mail.track;

import java.util.Deque;

public class User {
    private String name;
    private String pass;
    private String nickname;

    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
        this.nickname = name;
    }
    public User(String name, String nickname, String pass) {
        this.name = name;
        this.pass = pass;
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname( String nickname) {
        this.nickname = nickname;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
