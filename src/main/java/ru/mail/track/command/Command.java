package ru.mail.track.command;



import ru.mail.track.Message;
import ru.mail.track.server.Session;

public interface Command {
    Message execute(Session session, Message message);
}
