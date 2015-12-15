package ru.mail.track.command;

import ru.mail.track.ConnectionHandler;
import ru.mail.track.Message;
import ru.mail.track.MessageListener;
import ru.mail.track.server.Session;
import ru.mail.track.server.MessageStorage;
import ru.mail.track.server.UserStorage;

import java.util.Map;

/**
 * Created by egor on 12.11.15.
 */
public class CommandHandler implements MessageListener {
    Map<CommandType, Command> commands = CommandFiller.fillTypeMap();
    ConnectionHandler handler;

    public CommandHandler(ConnectionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(Session session, Message message) {
        Command cmd = commands.get(message.getType());
        try {
            handler.send(cmd.execute(session, message));
        } catch (Exception e) {
            System.out.println("Failed to respond ");
            e.printStackTrace();
        }
    }
}
