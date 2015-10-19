package ru.mail.track.command;



import ru.mail.track.Session;

import java.io.IOException;

public interface Command {
    void execute(Session session, String[] tokens);

    boolean checkTokens(Session session, String[] tokens);
}
