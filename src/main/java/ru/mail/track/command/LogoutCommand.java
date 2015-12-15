package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

public class LogoutCommand extends BaseCommand implements Command {

    int minArgNumber = 0;
    int maxArgNumber = 0;
    @Override
    public Message execute(Session session, Message message) {
        List<String> args = message.getArgs();
        if (args.size() > maxArgNumber || args.size() < minArgNumber) {
            return wrongArgumentNumber(args.size(), message.getId());
        }

        List<String> respondArgs = new ArrayList<>();
        String messageText;
        CommandType type;

        if (session.getIsLogin()) {
            messageText = "Bye, " + session.getUser().getName();
            session.setIsLogin(false);
            session.sessionManager.forgetUser(session.getUser().getId());
            session.setUser(null);
            type = CommandType.RESPOND_OK;
        } else {
            messageText = "Error: you are not authorized.";
            type = CommandType.RESPOND_ERROR;
        }

        return new Message(messageText, type, message.getId(), respondArgs);
    }

}
