package ru.mail.track.command;

import ru.mail.track.Session;


public class HelpCommand implements Command{
    public void execute(Session session, String[] tokens) {
        System.out.println ("Available commands: \n" +
                "\"/e\" or \"/exit\" - exit program \n" +
                "\"/user NICKNAME\" - set NICKNAME as user's nickname \n" +
                "\"/history X \" - show last X messages \n" +
                "\"/history\" - show all history \n" +
                "\"/h\" or \"/help\" - show available commands \n" +
                "\"/l NAME PASSWORD\" or \"/login NAME PASSWORD\" - login as NAME using PASSWORD as password \n" +
                "\"/c NAME PASSWORD\" or \"/create LOGIN PASSWORD\" - create new user with LOGIN and PASSWORD \n");
    }
    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 1;
    }
}
