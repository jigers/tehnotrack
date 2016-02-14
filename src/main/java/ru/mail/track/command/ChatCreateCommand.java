package ru.mail.track.command;


import ru.mail.track.Message;
import ru.mail.track.server.Session;
import ru.mail.track.server.User;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.server.Chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatCreateCommand extends BaseCommand implements Command {
    int minArgNumber = 1;
    int maxArgNumber = 1;
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
            String[] users = args.get(0).split(",");
            List<Integer> usersId = new ArrayList<>();
            for (String user : users) {
                usersId.add(Integer.parseInt(user));
            }
            usersId.add(session.getUser().getId());
            Chat chat;
            List<User> chatUsers = new ArrayList<>();


            if (usersId.size() == 2 && session.chatStorage.exist(usersId) > -1) {
                //2 users in the chat and this chat has already exist
                chat = session.chatStorage.getChat(session.chatStorage.exist(usersId));
                messageText = "Error: chat with those users have already exist. ID = "
                        + Integer.toString(chat.getId());
                type = CommandType.RESPOND_ERROR;
            } else {
                //creating new chat
                usersId.forEach(id -> chatUsers.add(session.userStorage.getUser(id)));
                chat = new Chat(chatUsers, session.getUser(), session.chatStorage.size() + 1); //TODO: chat id chose
                session.chatStorage.add(chat);
                //notification to all users in new chat, except the leader,
                Message msg = new Message("You were added to a new chat with ID = " + chat.getId(), CommandType.MSG, 0, null);

                chat.getUsers().forEach(user -> {
                    try {
                        if (user.getId() != chat.getLeader().getId()) {
                            session.sessionManager.getSessionByUser(user.getId()).getConnectionHandler().send(msg);
                            //TODO! behaviour if no session for this user
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); //TODO
                    } catch (ProtocolException e) {
                        e.printStackTrace(); //TODO
                    }
                });
                //respond
                messageText = "Chat was successfully created. ID = " + Integer.toString(chat.getId());
                type = CommandType.RESPOND_OK;
            }
        }
        return new Message(messageText, type, message.getId(), null);
    }
}
