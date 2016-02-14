package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 21.11.15.
 */
public class BaseCommand implements Command {

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }

    public Message wrongArgumentNumber(int got, int id) {
        List<String> respondArgs = new ArrayList<>();
        return new Message("Error: wrong number of arguments. Got: "
                + Integer.toString(got), CommandType.RESPOND_ERROR, id, respondArgs);
    }
}
