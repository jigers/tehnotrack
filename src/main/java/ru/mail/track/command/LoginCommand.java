package ru.mail.track.command;


import ru.mail.track.*;
import ru.mail.track.server.Session;
import ru.mail.track.server.User;

import java.util.ArrayList;
import java.util.List;

public class LoginCommand extends BaseCommand implements Command {
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
            //TODO too many requests to BD
            User user = session.userStorage.getUser(args.get(0));
            if (user != null && user.getPass().equals(args.get(1))) {
                if (session.sessionManager.userConnected(user.getId())) {
                    messageText = "Error: this user authorized in another session!";
                    type = CommandType.RESPOND_ERROR;
                } else {
                    session.setIsLogin(true);
                    session.setUser(user);
                    session.sessionManager.registerUser(user.getId(), session);
                    messageText = "Hello, " + user.getName() + "\nCurrent nickname: " + user.getNickname()
                            + "\nYour ID = " + Integer.toString(user.getId());
                    type = CommandType.RESPOND_OK;
                }
            } else {
                messageText = "Error: wrong login or password.";
                type = CommandType.RESPOND_ERROR;
            }

        } else {
            messageText = "Error: you are currently logged in.";
            type = CommandType.RESPOND_ERROR;
        }
        Message msg = new Message( messageText, type, message.getId(), respondArgs);
        return msg;
    }

}
