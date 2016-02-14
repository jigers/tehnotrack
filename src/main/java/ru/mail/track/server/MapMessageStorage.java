package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;
import ru.mail.track.Message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by egor on 30.11.15.
 */
/*
@Deprecated
public class MapMessageStorage implements MessageStorage {
    private Map<Integer, User> author = new HashMap<>();
    private List<Message> messages = new LinkedList<>();


    @Override
    public void init(PGPoolingDataSource source) throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void add(int chatId, User user, Message message) {
        this.author.put(message.getId(), user);
        this.messages.add(message);
    }

    @Override
    public List<Message> find(int chatId, User user, String sample) {
        List<Message> result = new LinkedList<>();
        for (Message msg : this.messages) {
            System.out.println(msg.getArgs().get(1));
            if (((user != null && msg.getId() == user.getId()) || user == null) && msg.getArgs().get(1).contains(sample)) {
                result.add(msg);
            };
        }
        return result;
    }

    @Override
    public List<Message> getLastMessages(int chatId, User user, int number) {
        return this.messages.subList(messages.size() - Math.min(number, messages.size()), messages.size());
    }

    @Override
    public User getAuthor(int messageId) {
        return this.author.get(messageId);
    }

    @Override
    public int getSize() {
        return this.messages.size();
    }
}*/
