package ru.mail.track.command;


import ru.mail.track.Session;
import ru.mail.track.User;

import java.util.Scanner;

public class LoginCommand implements Command {
    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            System.out.println("Error: you are logged already. Type \"/logout\" to logout or \"/help\" for help.");
            return;
        } else {
            if (tokens.length == 3) { //логин-пароль переданы в качестве аргументов
                login(session, tokens[1], tokens[2]);
            }
            if (tokens.length == 1) { //логин-пароль не переданы, требуется их ввод
                System.out.print("Login: ");
                Scanner scanner = new Scanner(System.in);
                String name = scanner.next();
                System.out.print("Password: ");
                String password = scanner.next();

                login(session, name, password);
            }
        }
    }
    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 3 || tokens.length == 1;
    }
    public void login(Session session, String name, String password) {
        if (session.userStorage.isUserExist(name)) {
            User user = session.userStorage.getUser(name, password);
            session.setUser(user);
            session.setIsLogin(true);
            System.out.println("Hello, " + user.getNickname());
        } else {
            System.out.println("Wrong login or password.");
        }
    }
}
