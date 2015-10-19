package ru.mail.track;

import ru.mail.track.command.Command;

import java.io.IOException;
import java.util.Map;


public class InputHandler {
    private Session session;
    private Map<String, Command> commands;
    public InputHandler(Session session, Map<String, Command> commands) {
        this.session = session;
        this.commands = commands;
    }

    public void handle (String data) {
        if (data.startsWith("/")) {

            String[] tokens = data.split(" ");
            if (commands.get(tokens[0]) != null) {
                Command cmd = commands.get(tokens[0]);

                if (cmd.checkTokens(session, tokens)) {
                    cmd.execute(session, tokens);
                } else {
                    System.out.println("Error: incorrect arguments for \"" + tokens[0] + "\". Type \"/help\" for help.");
                }
            } else {
                System.out.println("Error: wrong command.");
            }
        } else {
            if (session.getIsLogin()) {
                session.messageStorage.add(session.getUser(), data);
                System.out.println(">" + data);
            }
        }
    }
}
