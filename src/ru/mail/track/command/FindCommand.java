package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.Session;

import java.util.LinkedList;


public class FindCommand implements Command {
    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            LinkedList<Message> results = session.messageStorage.find(session.getUser(), tokens[1]);
            System.out.println("Found " + Integer.toString(results.size()) + " messages:");
            for (int i = 0; i < results.size(); ++i) {
                System.out.println("  " + session.getUser().getNickname() + " at " + results.get(i).getTime() + ": \n" +
                        "  >" + results.get(i).getMessage());
            }
        } else {
            System.out.println("Error: you are not authorized.");
        }
    }
    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 2;
    }

}
