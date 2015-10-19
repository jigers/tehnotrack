package ru.mail.track;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UserStorage {

    private LinkedList<User> users = new LinkedList<>();



    private String filepath;

    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public boolean isUserExist(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }


    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String name, String pass) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().compareTo(name) == 0 && users.get(i).getPass().compareTo(pass) == 0) {
                return users.get(i);
            }
        }
        return null;
    }
    public User getUser(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().compareTo(name) == 0) {
                return users.get(i);
            }
        }
        return null;
    }

    public void writeToFile () {
        try {
            PrintWriter out = new PrintWriter(filepath);
            for (int i = 0; i < users.size(); ++i) {
                out.println(users.get(i).getName() + " " + users.get(i).getNickname() + " " + users.get(i).getPass());
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: failed to write UserStorage to file.");
        }
    }

    public void loadFromFile () {
        Scanner scanner;
        try {
            scanner = new Scanner(Paths.get(filepath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] items = line.split(" ");
                User newUser = new User(items[0], items[1], items[2]);
                users.addFirst(newUser);
            }
        } catch (IOException e) {
            System.out.println("Error: UserStorage failed to load from file.");
        }
    }
}
