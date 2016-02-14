package ru.mail.track.command;

import ru.mail.track.Message;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;


public class HelpCommand extends BaseCommand implements Command {

    int minArgNumber = 0;
    int maxArgNumber = 0;

    public void execute(Session session, String[] tokens) {
        System.out.println ();
    }
    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 1;
    }


    @Override
    public Message execute(Session session, Message message) {
        List<String> args = message.getArgs();
        if (args.size() > maxArgNumber && args.size() < minArgNumber) {
            return wrongArgumentNumber(args.size(), message.getId());
        }
        List<String> respondArgs = new ArrayList<>();
        String messageText = "Available commands: \n" +
                "\"/e\" or \"/exit\" - exit program \n" +
                "\"/user NICKNAME\" - set NICKNAME as user's nickname \n" +
                "\"/history X \" - show last X messages \n" +
                "\"/history\" - show all history \n" +
                "\"/h\" or \"/help\" - show available commands \n" +
                "\"/l NAME PASSWORD\" or \"/login NAME PASSWORD\" - login as NAME using PASSWORD as password \n" +
                "\"/c NAME PASSWORD\" or \"/create LOGIN PASSWORD\" - create new user with LOGIN and PASSWORD \n";
        CommandType type = CommandType.RESPOND_OK;
        return new Message(messageText, type, message.getId(), respondArgs);
    }


}
