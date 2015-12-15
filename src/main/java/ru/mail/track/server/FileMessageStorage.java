package ru.mail.track.server;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.postgresql.ds.PGPoolingDataSource;
import ru.mail.track.Message;

import java.util.*;

@Deprecated
public class FileMessageStorage implements MessageStorage {
    private Map<String, LinkedList<Message>> messages = new HashMap<>();

    private String filepath;

    private void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void init(List<String> args) throws Exception {
        if (args.size() != 1) {
            throw new WrongNumberArgsException("1");
        } else {
            setFilepath(args.get(0));
        }
        loadFromFile();
    }

    @Override
    public void init(PGPoolingDataSource source) throws Exception {

    }

    @Override
    public void close() throws Exception {
        writeToFile();
    }

    @Override
    public void add(int chatId, User user, Message message) {

    }

    @Override
    public List<Message> find(int chatId, User user, String sample) {
        return null;
    }

    @Override
    public List<Message> getLastMessages(int chatId, User user, int number) {
        return null;
    }

    public void add(User user, Message message) {
        if (messages.get(user) != null) {
            messages.get(user).addFirst(message);
        } else {
            LinkedList<Message> newUserMessages = new LinkedList<>();
            newUserMessages.addFirst(message);
            messages.put(user.getName(), newUserMessages);
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

    public LinkedList<Message> getLastMessages(User user, int number) {
        LinkedList<Message> history = new LinkedList (messages.get(user).subList(0, Math.min(messages.get(user).size(), number)));
        Collections.reverse(history);
        return history;
    }

    @Override
    public User getAuthor(int messageId) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    private void writeToFile() throws Exception {
        /*
        PrintWriter out = new PrintWriter(filepath);
        for (Map.Entry<String, LinkedList<Message>> entry : messages.entrySet()) {
            out.println(entry.getKey() + " " + Integer.toString(entry.getValue().size()));
            for (int i = 0; i < entry.getValue().size(); ++i) {
                out.println(entry.getValue().get(i).getTime().toString());
                out.println(entry.getValue().get(i).getMessage());
                out.println(entry.getValue().get(i).getType());
                out.println(entry.getValue().get(i).ge);
            }
        }
        out.close();*/
    }

    private void loadFromFile () throws Exception {
        /*Scanner scanner = new Scanner(Paths.get(filepath));
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
            messages.put(items[0], list);
        }*/
        messages = new HashMap<>();

    }

}
