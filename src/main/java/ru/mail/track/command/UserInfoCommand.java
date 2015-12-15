package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 30.11.15.
 */
public class UserInfoCommand extends BaseCommand implements Command {
    private int minArgNumber = 0;
    private int maxArgNumber = 1;

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
            int userId;
            if (args.size() == 0) {
                userId = session.getUser().getId();
            } else {
                userId = Integer.parseInt(args.get(0));
            }
            messageText = "ID: " + Integer.toString(userId) + "\nNickname: " + session.userStorage.getUser(userId).getNickname();
            type = CommandType.RESPOND_OK;
        }
        return new Message(messageText, type, message.getId(), null);
    }
}
