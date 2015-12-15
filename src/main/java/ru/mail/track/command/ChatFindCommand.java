package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.MessageStorage;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ChatFindCommand extends BaseCommand implements Command {
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
            MessageStorage messageStorage = session.chatStorage.getChat(Integer.parseInt(args.get(0))).messageStorage;
            List<Message> results = messageStorage.find(Integer.parseInt(args.get(0)), null, args.get(1));
            StringBuilder stringBuilder = new StringBuilder("Found " + Integer.toString(results.size()) + " messages:\n");
            for (int i = 0; i < results.size(); ++i) {
                stringBuilder.append("  " + messageStorage.getAuthor(results.get(i).getId()).getNickname() + " at "
                        + results.get(i).getTime() + ": \n  >" + results.get(i).getArgs().get(1) + "\n");
            }
            messageText = stringBuilder.toString();
            type = CommandType.RESPOND_OK;
        }
        return new Message(messageText, type, message.getId(), null);
    }

}
