package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.server.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by egor on 24.11.15.
 */
public class ChatSayCommand extends BaseCommand implements Command {
    int minArgNumber = 2;
    int maxArgNumber = 2;
    @Override
    public Message execute(Session session, Message message) {
        List<String> args = message.getArgs();
        if (args.size() > maxArgNumber || args.size() < minArgNumber) {
            return wrongArgumentNumber(args.size(), message.getId());
        }
        String messageText;
        List<String> respondArgs = new ArrayList<>();
        CommandType type;
        if (!session.getIsLogin()) {
            messageText = "Error: you are not authorized.";
            type = CommandType.RESPOND_ERROR;
        } else {
            int chatId = Integer.parseInt(args.get(0));
            Chat chat = null;
            if (session.chatStorage.exist(chatId)) {
                chat = session.chatStorage.getChat(chatId);
            }

            if (chat != null) {
                //chat exist && user in it


                session.messageStorage.add(chatId, session.getUser(), message);
                //send message to other users
                String notificationText = "[chatID=" + Integer.toString(chatId) + "] " +
                        session.getUser().getNickname() + ": " + args.get(1);
                Message msg = new Message(notificationText, CommandType.MSG, 0, null);
                chat.getUsers().forEach(user -> {
                    try {
                        if (session.sessionManager.userConnected(user.getId()) &&
                                user.getId() != session.getUser().getId()) {
                            session.sessionManager.getSessionByUser(user.getId()).getConnectionHandler().send(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                        //TODO
                    }
                });
                messageText = null;
                type = CommandType.RESPOND_OK;
            } else {
                messageText = "Error: chat with this ID doesn't exist or you are not a participant of it.";
                type = CommandType.RESPOND_ERROR;
            }
        }

        return new Message(messageText, type, message.getId(), respondArgs);


    }
}
