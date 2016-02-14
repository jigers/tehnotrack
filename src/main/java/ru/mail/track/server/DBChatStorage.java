package ru.mail.track.server;

import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.command.CommandHandler;

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
    private boolean verbose = true; //true for DB logs

    static Logger log = LoggerFactory.getLogger(DBChatStorage.class);
    @Override
    public void add(Chat chat) {
        if (verbose) {
            log.info("Adding chat to DB with ID = " + Integer.toString(chat.getId()));
        }
        Connection connection;
        try {
            connection = source.getConnection();

            //adding chat to chatstorage table
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO chatstorage(id, admin_id)  values(?, ?)");
            preparedStatement.setInt(1, chat.getId());
            preparedStatement.setInt(2, chat.getLeader().getId());
            preparedStatement.executeUpdate();

            //adding chat to chatusers table
            PreparedStatement chatUsers = connection.prepareStatement(
                    "INSERT INTO chatusers(user_id, chat_id) values(?, ?)"
            );
            chatUsers.setInt(2, chat.getId());
            chat.getUsers().forEach(user -> {
                try {
                    chatUsers.setInt(1, user.getId());
                    chatUsers.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            size.addAndGet(1);
            connection.close();
        } catch (SQLException e) {
            log.info("Failed to add chat with ID = " + Integer.toString(chat.getId()));
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
            connection.close();
        } catch (SQLException e) {
            log.info("Failed to init DBChatStorage.");
            e.printStackTrace();
        }
        log.info("DBChatStorage successfully initialized.");
    }

    @Override
    public int exist(List<Integer> usersId) {
        if (verbose) {
            log.info("Checking chat existance (by list of users).");
        }
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
                preparedStatementUsers.setInt(1, resultChats.getInt("id"));
                ResultSet resultUsers = preparedStatementUsers.executeQuery();
                while (resultUsers.next()) {
                    users.add(resultUsers.getInt("user_id"));
                }
                if (usersId.containsAll(users) && users.containsAll(usersId)) {
                    return resultChats.getInt("id");
                }
            }
            connection.close();
            return -1;
        } catch (SQLException e) {
            log.info("Failed to check chat existance (list of users)");
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void remove(Chat chat) throws Exception {

    }

    @Override
    public Chat getChat(int id) {
        if (verbose) {
            log.info("Trying to get chat with ID = " + Integer.toString(id));
        }
        Connection connection = null;
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
            if (verbose) {
                log.info("Extracted " + Integer.toString(users.size()) + " users for chat with ID = " + Integer.toString(id));
            }
            resultChat.next();
            userInfo.setInt(1, resultChat.getInt("admin_id"));
            ResultSet userInfoResult = userInfo.executeQuery();
            userInfoResult.next();
            connection.close();
            return new Chat(users, new User(userInfoResult.getInt("id"), userInfoResult.getString("login"),
                    userInfoResult.getString("pass"), userInfoResult.getString("nickname")), id);
        } catch (SQLException e) {
            log.info("Failed to get chat with ID = " + Integer.toString((id)));
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
        if (verbose) {
            log.info("Trying to check chat existance with ID = " + Integer.toString(chatId));
        }
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM chatstorage WHERE id=?");
            preparedStatement.setInt(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.close();
            return resultSet.next();
        } catch (SQLException e) {
            log.info("Failed to check chat existance (chat id)");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Chat> getChatList(int userId) {
        if (verbose) {
            log.info("Trying to get chat list for user with ID  = " + Integer.toString(userId));
        }
        Connection connection;
        List<Chat> chatList = new ArrayList<>();
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT DISTINCT chat_id FROM chatusers WHERE user_id=?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            connection.close();
            while (resultSet.next()) {
                chatList.add(this.getChat(resultSet.getInt("chat_id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to get chat list for user with ID = " + Integer.toString(userId));
        }
        return chatList;
    }

    @Override
    public void close() {

    }
}
