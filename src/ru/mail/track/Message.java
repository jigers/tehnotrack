package ru.mail.track;


import java.sql.Timestamp;
/**
 * Created by egor on 15.10.15.
 */
public class Message {


    private String message;
    private Timestamp time;
    Message(String message) {
        this.message = message;
        this.time = new Timestamp(System.currentTimeMillis());
    }
    Message(String message, Timestamp time) {
        this.message = message;
        this.time = time;
    }
    public String getMessage() {
        return message;
    }

    public Timestamp getTime() {
        return time;
    }
}
