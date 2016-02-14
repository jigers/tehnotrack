package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.List;

/**
 * Created by egor on 30.11.15.
 */
public class ChatListCommand extends BaseCommand implements Command {
    int minArgNumber = 0;
    int maxArgNumber = 0;
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
            StringBuilder builder = new StringBuilder();
            session.chatStorage.getChatList(session.getUser().getId()).forEach(chat ->
                    builder.append("\n" + chat.getId())
            );
            messageText = "You entered next chates: " + builder.toString();
            type = CommandType.RESPOND_OK;
        }
        return new Message(messageText, type, message.getId(), null);
    }
}
