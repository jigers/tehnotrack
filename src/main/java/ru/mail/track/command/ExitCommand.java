package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

public class ExitCommand extends BaseCommand implements Command {
    @Override
    public Message execute(Session session, Message message) {
        session.sessionManager.forgetUser(session.getUser().getId());
        session.getConnectionHandler().stop();
        return new Message("Exiting...", CommandType.RESPOND_OK, message.getId(), null);
    }

}
