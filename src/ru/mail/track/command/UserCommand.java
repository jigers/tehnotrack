package ru.mail.track.command;

import ru.mail.track.Session;

public class UserCommand implements Command {
    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            session.getUser().setNickname(tokens[1]);
            System.out.println("Nickname successfully changed to \"" + tokens[1] + "\"");
        } else {
            System.out.println("Error: you are not authorized.");
        }
    }
    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 2;
    }
}
