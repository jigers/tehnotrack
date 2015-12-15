package ru.mail.track.command;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandFiller {
    public static Map<CommandType, Command> fillTypeMap () {
        Map<CommandType, Command> commands = new HashMap<>();
        commands.put(CommandType.USER_CREATE, new CreateCommand());
        commands.put(CommandType.USER_LOGIN, new LoginCommand());
        commands.put(CommandType.USER_EXIT, new ExitCommand());
        commands.put(CommandType.CHAT_HISTORY, new ChatHistoryCommand());
        commands.put(CommandType.USER_HELP, new HelpCommand());
        commands.put(CommandType.USER_LOGOUT, new LogoutCommand());
        commands.put(CommandType.USER_USERNAME, new UsernameCommand());
        commands.put(CommandType.USER_INFO, new UserInfoCommand());
        commands.put(CommandType.USER_PASS, new UserPassCommand());
        commands.put(CommandType.CHAT_CREATE, new ChatCreateCommand());
        commands.put(CommandType.CHAT_FIND, new ChatFindCommand());
        commands.put(CommandType.CHAT_SAY, new ChatSayCommand());
        commands.put(CommandType.CHAT_LIST, new ChatListCommand());
        return commands;
    }

    public static Map<String, CommandType> fillStringMap() {
        Map<String, CommandType> commands = new HashMap<>();
        commands.put("/l", CommandType.USER_LOGIN);
        commands.put("/login", CommandType.USER_LOGIN);
        commands.put("/c", CommandType.USER_CREATE);
        commands.put("/create", CommandType.USER_CREATE);
        commands.put("/e", CommandType.USER_EXIT);
        commands.put("/exit", CommandType.USER_EXIT);
        commands.put("/logout", CommandType.USER_LOGOUT);
        commands.put("/username", CommandType.USER_USERNAME);
        commands.put("/help", CommandType.USER_HELP);
        commands.put("/info", CommandType.USER_INFO);
        commands.put("/user_info", CommandType.USER_INFO);
        commands.put("/user_pass", CommandType.USER_PASS);
        commands.put("/pass", CommandType.USER_PASS);
        commands.put("/history", CommandType.CHAT_HISTORY);
        commands.put("/chat_history", CommandType.CHAT_HISTORY);
        commands.put("/find", CommandType.CHAT_FIND);
        commands.put("/chat_find", CommandType.CHAT_FIND);
        commands.put("/chat_say", CommandType.CHAT_SAY);
        commands.put("/say", CommandType.CHAT_SAY);
        commands.put("/chat_create", CommandType.CHAT_CREATE);
        commands.put("/chat_list", CommandType.CHAT_LIST);
        commands.put("/list", CommandType.CHAT_LIST);
        return commands;
    }
}
