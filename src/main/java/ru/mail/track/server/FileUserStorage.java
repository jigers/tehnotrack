package ru.mail.track.server;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.postgresql.ds.PGPoolingDataSource;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
/*
@Deprecated
public class FileUserStorage implements UserStorage {
    private LinkedList<User> users = new LinkedList<>();

    private String filepath;

    private void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    @Override
    public void init(PGPoolingDataSource source) throws Exception {

    }

    public void close() throws FileNotFoundException{
        writeToFile();
    }
    public void init(List <String> args) throws Exception{
        if (args.size() > 0) {
            setFilepath(args.get(0));
        } else {
            throw new WrongNumberArgsException("1");
        }
        loadFromFile();
    }

    private void writeToFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(filepath);
        for (int i = 0; i < users.size(); ++i) {
            out.println(users.get(i).getName() + " " + users.get(i).getNickname() + " " + users.get(i).getPass());
        }
        out.close();
    }

    private void loadFromFile() throws Exception{
        Scanner scanner;
        scanner = new Scanner(Paths.get(filepath));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] items = line.split(" ");
            /*User newUser = new User(items[0], items[1], items[2]);
            users.addFirst(newUser);
        }
    }
    public boolean isUserExist(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addUser(String name, String pass) {

    }

    public void addUser(User user) {
        users.add(user);
    }

    public User getUser(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (users.get(i).getName().compareTo(name) == 0) {
                return users.get(i);
            }
        }
        return null;
    }

    @Override
    public User getUser(int id) {
        return null;
    }
}
*/