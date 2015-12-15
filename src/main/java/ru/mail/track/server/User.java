package ru.mail.track.server;

public class User {
    private String name;
    private String pass;
    private String nickname;
    private int id;

    public int getId() {
        return id;
    }

    public User(int id, String name, String pass) {
        this.name = name;
        this.pass = pass;
        this.nickname = name;
        this.id = id;
    }

    public User(int id, String name, String pass, String nickname) {
        this.id = id;
        this.name = name;
        this.pass = pass;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
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
