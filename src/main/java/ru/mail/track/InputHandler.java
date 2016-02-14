package ru.mail.track;

import ru.mail.track.command.Command;
import ru.mail.track.server.Session;

import java.util.Map;

@Deprecated
public class InputHandler {
    private Session session;
    private Map<String, Command> commands;
    public InputHandler(Session session, Map<String, Command> commands) {
        this.session = session;
        this.commands = commands;
    }

}
