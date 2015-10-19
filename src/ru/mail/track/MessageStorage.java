package ru.mail.track;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;
import java.sql.Timestamp;

public class MessageStorage {
    private Map<User, LinkedList<Message>> messages = new HashMap<User, LinkedList<Message>>();
    private String filepath;
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public void add(User user, String text) {
        Message message = new Message(text);
        if (messages.get(user) != null) {
            messages.get(user).addFirst(message);
        } else {
            LinkedList<Message> newUserMessages = new LinkedList<Message>();
            newUserMessages.addFirst(message);
            messages.put(user, newUserMessages);
        }
    }
    public LinkedList<Message> find(User user, String sample) {
        LinkedList<Message> result = new LinkedList<Message>();
        ListIterator<Message> iterator = messages.get(user).listIterator(0);
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            //в разработке
            /*String regexp = ".*" + sample + ".*";
            if (msg.getMessage().matches(regexp)) {
                result.addFirst(msg);
            }*/
            if (msg.getMessage().contains(sample)) {
                result.addFirst(msg);
            }
        }
        return result;
    }
    public LinkedList<Message> get(User user, int number) {
        LinkedList<Message> history = new LinkedList (messages.get(user).subList(0, Math.min(messages.get(user).size(), number)));
        Collections.reverse(history);
        return history;
    }
    public int getSize(User user) {
        return messages.get(user).size();
    }

    public void writeToFile () {

        try {
            PrintWriter out = new PrintWriter(filepath);
            for (Map.Entry<User, LinkedList<Message> > entry : messages.entrySet()) {
                out.println(entry.getKey().getName()  + " " + Integer.toString(entry.getValue().size()));
                for (int i = 0; i < entry.getValue().size(); ++i ) {
                    out.println(entry.getValue().get(i).getTime().toString());
                    out.println(entry.getValue().get(i).getMessage());
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: failed to write MessageStorage to file.");
        }
    }

    public void loadFromFile (Session session) {
        try {
            Scanner scanner = new Scanner(Paths.get(filepath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] items = line.split(" ");
                LinkedList<Message> list = new LinkedList<Message>();
                for (int i = 0; i < Integer.parseInt(items[1]); ++i) {
                    String time = scanner.nextLine();
                    Timestamp timestamp = Timestamp.valueOf(time);
                    String message = scanner.nextLine();
                    list.addFirst(new Message(message, timestamp));
                }
                messages.put(session.userStorage.getUser(items[0]), list);
            }
        } catch (IOException e) {
            System.out.println("Error: MessageStorage failed to load from file.");
        }
    }
}
