package ru.mail.track;


import ru.mail.track.command.CommandType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by egor on 15.10.15.
 */
public class Message implements Serializable{

    private String message;
    private Timestamp time;
    private CommandType type;
    private int id;
    private List<String> args;

    public Message(String message, CommandType type, int id, List<String> args) {
        this.type = type;
        this.message = message;
        this.id = id;
        this.args = args;
        this.time = new Timestamp(System.currentTimeMillis());
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }

    public CommandType getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        getArgs().forEach(arg -> builder.append(arg + " "));
        return Integer.toString(id) + " " + message + " " + type + " " + time + " " + builder.toString();
    }

}
