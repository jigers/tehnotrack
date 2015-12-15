package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;
import ru.mail.track.Message;
import ru.mail.track.command.CommandType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by egor on 01.12.15.
 */
public class DBMessageStorage implements MessageStorage {
    private PGPoolingDataSource source;

    @Override
    public void init(PGPoolingDataSource source) throws Exception {
        this.source = source;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void add(int chatId, User user, Message message) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO messagestorage(id, time, author_id, text, chat_id)  values(?, ?, ?, ?, ?)");
            preparedStatement.setInt(1, message.getId());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.setString(4, message.getArgs().get(1));
            preparedStatement.setInt(5, chatId);
            preparedStatement.setTimestamp(2, message.getTime());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> find(int chatId, User user, String sample) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM messagestorage WHERE chat_id=?, user_id=?");
            preparedStatement.setInt(1, chatId);
            preparedStatement.setInt(2, user.getId());
            List<Message> ans = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String text = resultSet.getString("text");
                if (text.contains(sample)) {
                    ans.add(new Message(null, CommandType.CHAT_SAY, resultSet.getInt("id"),
                            new ArrayList<String>(Arrays.asList(Integer.toString(chatId), text))));
                }
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public List<Message> getLastMessages(int chatId, User user, int number) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT TOP(?) FROM messagestorage WHERE chat_id=?, user_id=?" +
                            "ORDER BY time DESC");
            preparedStatement.setInt(2, chatId);
            preparedStatement.setInt(3, user.getId());
            preparedStatement.setInt(1, number);
            List<Message> ans = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String text = resultSet.getString("text");
                ans.add(new Message(null, CommandType.CHAT_SAY, resultSet.getInt("id"),
                            new ArrayList<String>(Arrays.asList(Integer.toString(chatId), text))));
            }
            return ans;
        } catch (SQLException e) {
            e.printStackTrace(); //TODO
        }
        return null;
    }

    @Override
    public User getAuthor(int messageId) {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
