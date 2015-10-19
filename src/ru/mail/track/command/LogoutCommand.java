package ru.mail.track.command;

import ru.mail.track.Session;

public class LogoutCommand implements Command {
    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            System.out.println("Bye, " + session.getUser().getNickname() + ".");
            session.setIsLogin(false);
            session.setUser(null);
        }
    }

    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 1;
    }
}
