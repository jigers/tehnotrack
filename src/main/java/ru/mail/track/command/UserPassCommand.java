package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 30.11.15.
 */
public class UserPassCommand extends BaseCommand implements Command {
    int minArgNumber = 2;
    int maxArgNumber = 2;

    @Override
    public Message execute(Session session, Message message) {
        List<String> args = message.getArgs();
        if (args.size() > maxArgNumber || args.size() < minArgNumber) {
            return wrongArgumentNumber(args.size(), message.getId());
        }

        List<String> respondArgs = new ArrayList<>();
        String messageText;
        CommandType type;

        if (!session.getIsLogin()) {
            type = CommandType.RESPOND_ERROR;
            messageText = "Error: you are not authorized.";
        } else {
            if (session.getUser().getPass().equals(args.get(0))) {
                session.getUser().setPass(args.get(1));
                session.userStorage.changePass(session.getUser().getId(), args.get(1));
                type = CommandType.RESPOND_OK;
                messageText = "Password successfully changed.";
            } else {
                type = CommandType.RESPOND_ERROR;
                messageText = "Error: wrong password.";
            }
        }

        return new Message(messageText, type, message.getId(), null);
    }
}
