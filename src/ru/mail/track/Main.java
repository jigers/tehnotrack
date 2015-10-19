
package ru.mail.track;

import ru.mail.track.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        UserStorage userStorage = new UserStorage();
        userStorage.setFilepath("userStorage.txt");
        MessageStorage messageStorage = new MessageStorage();
        messageStorage.setFilepath("messageStorage.txt");

        Session session = new Session(userStorage, messageStorage);

        session.userStorage.loadFromFile();
        session.messageStorage.loadFromFile(session);

        Map<String, Command> commands = new HashMap<String, Command>();

        CommandFiller.fill(commands);


        InputHandler inputHandler = new InputHandler(session, commands);
        System.out.println("Welcome! Type \"/help\" for help");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            inputHandler.handle(line);
        }
    }
}
