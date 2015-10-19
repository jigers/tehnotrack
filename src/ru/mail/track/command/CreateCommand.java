package ru.mail.track.command;

import ru.mail.track.Session;
import ru.mail.track.User;

import java.util.Scanner;


public class CreateCommand implements Command {
    public void execute(Session session, String[] tokens) {
        if (session.getIsLogin()) {
            System.out.println("Error: you are logged already. Type \"/logout\" to logout or \"/help\" for help.");
        } else {
            if (tokens.length == 3) {
                create(session, tokens[1], tokens[2]);
            } else {
                System.out.print("Login: ");
                Scanner scanner = new Scanner(System.in);
                String name = scanner.next();
                System.out.print("Password: ");
                String password = scanner.next();
                create(session, name, password);
            }
        }
    }

    public boolean checkTokens(Session session, String[] tokens) {
        return tokens.length == 3 || tokens.length == 1;
    }
    private void create(Session session, String name, String password) {
        if (session.userStorage.isUserExist(name)) {
            System.out.println("Error: user already exists.");
        } else {
            if (password.length() > 4) {
                User newUser = new User(name, password);
                session.userStorage.addUser(newUser);
                session.setIsLogin(true);
                session.setUser(newUser);
                System.out.println("Hello, " + name);
            } else {
                System.out.println("Error: short password. Type \"/help\" for help.");
            }
        }
    }
}
