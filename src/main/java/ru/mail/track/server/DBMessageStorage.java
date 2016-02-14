package ru.mail.track.server;

import oracle.jdbc.proxy.annotation.Pre;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Message;
import java.sql.Timestamp;
import ru.mail.track.command.CommandHandler;
import ru.mail.track.command.CommandType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by egor on 01.12.15.
 */
public class DBMessageStorage implements MessageStorage {
    private PGPoolingDataSource source;
    private boolean verbose = false; //true for DB logs
    static Logger log = LoggerFactory.getLogger(DBMessageStorage.class);
    @Override
    public void init(PGPoolingDataSource source) throws Exception {
        this.source = source;
        log.info("DBMessageStorage ssuccessfully initialized.");
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void add(int chatId, User user, Message message) {
        Connection connection;
        if (verbose) {
            log.info("Adding message to DB: chatId = " + Integer.toString(chatId) + ", userId = " +
                    Integer.toString(user.getId()) + ", text = " + message.getArgs().get(1));
        }
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO messagestorage(id, time, author_id, message, chat_id)  values(?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, message.getId());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.setString(4, message.getArgs().get(1));
            preparedStatement.setInt(5, chatId);
            preparedStatement.setTimestamp(2, message.getTime());
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            log.info("Failed to add new message to DB: chatId = " + Integer.toString(chatId) + ", userId = " +
                    Integer.toString(user.getId()) + ", text = " + message.getArgs().get(1));
            e.printStackTrace();
        }
        if (verbose) {
            log.info("Finished adding message to DB: chatId = " + Integer.toString(chatId) + ", userId = " +
                    Integer.toString(user.getId()) + ", text = " + message.getArgs().get(1));
        }
    }

    @Override
    public List<Message> find(int chatId, String sample) {
        Connection connection;
        if (verbose) {
            log.info("Finding message in chat: chatId = " + Integer.toString(chatId) +
                    ", sample = " + sample);
        }
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM messagestorage WHERE chat_id=? ORDER BY time DESC");
            preparedStatement.setInt(1, chatId);
            List<Message> ans = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String text = resultSet.getString("message");
                if (text.contains(sample)) {
                    Integer messageId = resultSet.getInt("id");
                    Timestamp time = resultSet.getTimestamp("time");
                    List<String> args = new ArrayList<>();
                    args.add(Integer.toString(chatId));
                    args.add(text);
                    ans.add(new Message(new String(""), CommandType.CHAT_SAY, messageId, args, time));
                }
            }
            connection.close();
            Collections.reverse(ans);
            return ans;
        } catch (SQLException e) {
            log.info("Failed to find message in DB: chatId = " + Integer.toString(chatId) + ", sample = " + sample);
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public List<Message> getLastMessages(int chatId, int number) {
        Connection connection;
        if (verbose) {
            log.info("Getting last messages: chatId = " + Integer.toString(chatId) +
                    ", number = " + Integer.toString(number));
        }
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement;
            if (number != 0) {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM messagestorage WHERE chat_id=? ORDER BY \"time\" DESC LIMIT ?");
                preparedStatement.setInt(1, chatId);
                preparedStatement.setInt(2, number);
            } else {
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM messagestorage WHERE chat_id=? ORDER BY \"time\" DESC");
                preparedStatement.setInt(1, chatId);
            }
            List<Message> ans = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String text = resultSet.getString("message");
                Integer messageId = resultSet.getInt("id");
                Timestamp time = resultSet.getTimestamp("time");
                List<String> args = new ArrayList<>();
                args.add(Integer.toString(chatId));
                args.add(text);
                ans.add(new Message(new String(""), CommandType.CHAT_SAY, messageId, args, time));
            }
            connection.close();
            Collections.reverse(ans);
            return ans;
        } catch (SQLException e) {
            log.info("Failed to get last messages: chatId = " + Integer.toString(chatId)
                    + ", number = " + Integer.toString(number));
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public int getAuthor(int messageId) {
        Connection connection;
        if (verbose) {
            log.info("Getting author of message with ID = " + Integer.toString(messageId));
        }
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM messagestorage WHERE id = ?");
            preparedStatement.setInt(1, messageId);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            connection.close();
            return result.getInt("author_id");
        } catch (SQLException e) {
            log.info("Fail to get author of message with ID = " + Integer.toString(messageId));
            e.printStackTrace();
            return -1;
        }

    }

    @Override
    public int getSize() {
        return 0;
    }
}
