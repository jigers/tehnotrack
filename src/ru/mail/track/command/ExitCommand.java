package ru.mail.track.command;

import ru.mail.track.Session;

import java.io.IOException;

public class ExitCommand implements Command {

    public void execute(Session session, String[] tokens) {
        System.out.println("Exit...");
        session.saveData();
        System.exit(0);
    }

    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 1;
    }
}
