package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by egor on 01.12.15.
 */
public class DBChatStorage implements ChatStorage {
    private AtomicInteger size = new AtomicInteger(0);
    private PGPoolingDataSource source;

    @Override
    public void add(Chat chat) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO chatstorage(id, messagestorage_id, admin_id)  values(?, ?, ?)");
            preparedStatement.setInt(1, chat.getId());
            preparedStatement.setInt(2, chat.getId());
            preparedStatement.setInt(3, chat.getLeader().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(PGPoolingDataSource source) {
        this.source = source;
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM chatstorage");
            ResultSet results = preparedStatement.executeQuery();
            size = new AtomicInteger(0);
            while(results.next()) {
                size.incrementAndGet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int exist(List<Integer> usersId) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatementChats = connection.prepareStatement(
                    "SELECT * FROM chatstorage");
            PreparedStatement preparedStatementUsers = connection.prepareStatement(
                    "SELECT * FROM chatusers WHERE chat_id=?");
            ResultSet resultChats = preparedStatementChats.executeQuery();
            while(resultChats.next()) {
                List<Integer> users = new ArrayList<>();
                preparedStatementChats.setInt(1, resultChats.getInt("chat_id"));
                ResultSet resultUsers = preparedStatementUsers.executeQuery();
                while (resultUsers.next()) {
                    users.add(resultUsers.getInt("user_id"));
                }
                if (usersId.containsAll(users) && users.containsAll(usersId)) {
                    return resultChats.getInt("chat_id");
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void remove(Chat chat) throws Exception {

    }

    @Override
    public Chat getChat(int id) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatementChat = connection.prepareStatement(
                    "SELECT * FROM chatstorage WHERE id=?");
            PreparedStatement preparedStatementUsers = connection.prepareStatement(
                    "SELECT * FROM chatusers WHERE chat_id=?");
            PreparedStatement userInfo = connection.prepareStatement(
                    "SELECT * FROM userstorage WHERE id=?");
            preparedStatementChat.setInt(1, id);
            preparedStatementUsers.setInt(1, id);
            ResultSet chatUsers = preparedStatementUsers.executeQuery();
            ResultSet resultChat = preparedStatementChat.executeQuery();
            List<User> users = new ArrayList<>();
            while (chatUsers.next()) {
                userInfo.setInt(1, chatUsers.getInt("user_id"));
                ResultSet userInfoResultSet = userInfo.executeQuery();
                userInfoResultSet.next();
                users.add(new User(userInfoResultSet.getInt("id"), userInfoResultSet.getString("login"),
                        userInfoResultSet.getString("pass"), userInfoResultSet.getString("nickname")));
            }
            resultChat.next();
            userInfo.setInt(1, resultChat.getInt("admin_id"));
            ResultSet userInfoResult =  userInfo.executeQuery();
            return new Chat(users, new User(userInfoResult.getInt("id"), userInfoResult.getString("login"),
                    userInfoResult.getString("pass"), userInfoResult.getString("nickname")), id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public boolean exist(int chatId) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM chatstorage WHERE id=?");
            preparedStatement.setInt(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Chat> getChatList(int userId) {
        Connection connection;
        List<Chat> chatList = new ArrayList<>();
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT DISTINCT chat_id FROM chatusers WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                chatList.add(this.getChat(resultSet.getInt("chat_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatList;
    }
}
