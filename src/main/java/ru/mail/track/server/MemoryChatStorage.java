package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by egor on 24.11.15.
 */
@Deprecated
public class MemoryChatStorage implements ChatStorage {

    private Map<Integer, Chat> chats = new HashMap<>();

    @Override
    public void add(Chat chat) {
        this.chats.put(chat.getId(), chat);
    }

    @Override
    public void init(PGPoolingDataSource source) {

    }

    @Override
    public int exist(List<Integer> usersId) {
        for (Map.Entry<Integer, Chat> entry: chats.entrySet()) {
            if (entry.getValue().getUsers().size() == usersId.size()) {
                List<Integer> chatUsersId = new ArrayList<>();
                for (User user : entry.getValue().getUsers()) {
                    chatUsersId.add(user.getId());
                }
                if (chatUsersId.containsAll(usersId) && usersId.containsAll(chatUsersId)) {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    @Override
    public void remove(Chat chat) throws Exception {
        chats.remove(chat.getId());
    }

    @Override
    public Chat getChat(int id) {
        return chats.get(id);
    }

    @Override
    public int size() {
        return chats.size();
    }

    @Override
    public boolean exist(int chatId) {
        return chats.containsKey(chatId);
    }

    @Override
    public List<Chat> getChatList(int userId) {
        List<Chat> chatList = new ArrayList<>();
        for (Map.Entry<Integer, Chat> entry : chats.entrySet()) {
            List<User> users = entry.getValue().getUsers();
            for (User user : users) {
                if (user.getId() == userId) {
                    chatList.add(entry.getValue());
                    break;
                }
            }
        }
        return chatList;
    }

    @Override
    public void close() {

    }
}
