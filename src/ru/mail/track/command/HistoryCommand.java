package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.Session;
import ru.mail.track.User;

import java.util.LinkedList;

public class HistoryCommand implements Command {

    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            if (tokens.length == 2) {
                extractHistory(session, Integer.parseInt(tokens[1]));
            } else {
                extractHistory(session, session.messageStorage.getSize(session.getUser()));
            }
        } else {
            System.out.println("Error: you are not authorized.");
        }
    }
    private void extractHistory (Session session, int number) {
        LinkedList<Message> history = session.messageStorage.get(session.getUser(), number);
        System.out.println("Last " + Integer.toString(history.size()) + " messages: ");
        for (int i = 0; i < history.size(); ++i) {
            System.out.println("  " + session.getUser().getNickname() + " at " + history.get(i).getTime().toString() +
                    ": \n  >" + history.get(i).getMessage());
        }
    }
    public boolean checkTokens(Session session, String[] tokens) {
        if (tokens.length > 2) {
            return false;
        }
        if (tokens.length == 2) {
            try {
                int messageNum = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
