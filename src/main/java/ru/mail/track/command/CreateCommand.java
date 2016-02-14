package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;
import ru.mail.track.server.User;

import java.util.ArrayList;
import java.util.List;


public class CreateCommand extends BaseCommand implements Command {
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
        if (session.getIsLogin()) {
            messageText = "Error: you are currenlty logged in.";
            type = CommandType.RESPOND_ERROR;
        } else {
            if (session.userStorage.isUserExist(args.get(0))) {
                messageText = "Error: user with this name already exist.";
                type = CommandType.RESPOND_ERROR;
            } else {
                session.userStorage.addUser(args.get(0), args.get(1));
                User user = session.userStorage.getUser(args.get(0));
                session.setUser(user);
                session.setIsLogin(true);
                session.sessionManager.registerUser(session.getUser().getId(), session);
                messageText = "User successfully created. \nHello, " + user.getNickname() + "\nCurrent nickname: " + user.getNickname()
                        + "\nYour ID = " + Integer.toString(user.getId());
                type = CommandType.RESPOND_OK;
            }
        }
        Message msg = new Message(messageText, type, message.getId(), respondArgs);
        return msg;
    }


}
