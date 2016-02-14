package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;
import ru.mail.track.Message;

import java.util.List;

/**
 * Created by egor on 12.11.15.
 */
public interface MessageStorage {
    void init(PGPoolingDataSource source) throws Exception;

    void close() throws Exception;

    void add(int chatId, User user, Message message);

    List<Message> find(int chatId, String sample);

    List<Message> getLastMessages(int chatId, int number);

    int getAuthor(int messageId);

    int getSize();
}
