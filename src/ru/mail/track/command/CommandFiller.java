package ru.mail.track.command;

import java.util.Map;

public class CommandFiller {
    static public void fill(Map<String, Command> commands) {
        //пока что в таком виде...
        CreateCommand create = new CreateCommand();
        ExitCommand exit = new ExitCommand();
        HelpCommand help = new HelpCommand();
        HistoryCommand history = new HistoryCommand();
        LoginCommand login = new LoginCommand();
        LogoutCommand logout = new LogoutCommand();
        UserCommand user = new UserCommand();
        FindCommand find = new FindCommand();
        commands.put("/help", help);
        commands.put("/h", help);
        commands.put("/exit", exit);
        commands.put("/e", exit);
        commands.put("/history", history);
        commands.put("/logout", logout);
        commands.put("/login", login);
        commands.put("/l", login);
        commands.put("/create", create);
        commands.put("/c", create);
        commands.put("/user", user);
        commands.put("/find", find);
    }
}
