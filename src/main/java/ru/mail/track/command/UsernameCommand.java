package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

public class UsernameCommand extends BaseCommand implements Command {

    int minArgNumber = 1;
    int maxArgNumber = 1;
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
            type = CommandType.RESPOND_OK;
            session.getUser().setNickname(args.get(0));
            session.userStorage.updateNickname(session.getUser().getId(), args.get(0));
            messageText = "Nickname successfully changed to " + session.getUser().getNickname();
        }
        return new Message(messageText, type, message.getId(), respondArgs);
    }

}
