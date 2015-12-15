package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;

import java.util.List;

/**
 * Created by egor on 20.11.15.
 */
public interface ChatStorage {
    void add(Chat chat);

    void init(PGPoolingDataSource source);

    int exist(List<Integer> usersId); //return chatID with this user if it exist or -1 if not

    void remove(Chat chat) throws Exception; //TODO Exception type

    Chat getChat(int id);

    int size();

    boolean exist(int chatId);

    List<Chat> getChatList(int userId);
}
