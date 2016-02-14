package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.MessageStorage;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatHistoryCommand extends BaseCommand implements Command {
    private int minArgNumber = 1;
    private int maxArgNumber = 2;

    @Override
    public Message execute(Session session, Message message) {
        List<String> args = message.getArgs();
        if (args.size() > maxArgNumber || args.size() < minArgNumber) {
            return wrongArgumentNumber(args.size(), message.getId());
        }
        String messageText;
        CommandType type;
        if (!session.getIsLogin()) {
            type = CommandType.RESPOND_ERROR;
            messageText = "Error: you are not authorized.";
        } else {
            MessageStorage messageStorage = session.messageStorage;
            int messageNumber;
            if (args.size() == 1) {
                messageNumber = messageStorage.getSize();
            } else {
                messageNumber = Integer.parseInt(args.get(1));
            }

            List<Message> results = messageStorage.getLastMessages(Integer.parseInt(args.get(0)), messageNumber);
            StringBuilder builder = new StringBuilder("Last " + Integer.toString(results.size()) + " messages:\n");
            for (int i = 0; i < results.size(); ++i) {
                int author_id = messageStorage.getAuthor(results.get(i).getId());
                builder.append("  " + session.userStorage.getUser(author_id).getNickname() + " at "
                        + results.get(i).getTime() + ": \n  >" + results.get(i).getArgs().get(1) + "\n");
            }
            type = CommandType.RESPOND_OK;
            messageText = builder.toString();
        }
        return new Message(messageText, type, message.getId(), null);
    }
}
