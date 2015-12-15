package ru.mail.track.server;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.postgresql.ds.PGPoolingDataSource;

/**
 * Created by egor on 30.11.15.
 */
public class DBUserStorage implements UserStorage {
    private PGPoolingDataSource source;
    private AtomicInteger size = new AtomicInteger(0);

    @Override
    public void init(PGPoolingDataSource source) throws ClassNotFoundException{
        this.source = source;
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM userstorage");
            ResultSet results = preparedStatement.executeQuery();
            connection.close();
            size = new AtomicInteger(0);
            while(results.next()) {
                size.incrementAndGet();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public boolean isUserExist(String name) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM userstorage where login=?");
            preparedStatement.setString(1, name);
            ResultSet results = preparedStatement.executeQuery();
            connection.close();
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void addUser(String name, String pass) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO userStorage(id, login, pass, nickname)  values(?, ?, ?, ?)");
            preparedStatement.setInt(1, size.getAndIncrement());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, pass);
            preparedStatement.setString(4, name);
            preparedStatement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String name) {
        Connection connection;
        System.out.println("Trying get user with name " + name);
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM userStorage where login=?");
            preparedStatement.setString(1, name);
            ResultSet results = preparedStatement.executeQuery();
            connection.close();
            if (!results.next()) {
                return null;
            } else {
                return new User(results.getInt("id"), results.getString("login"), results.getString("pass"),
                        results.getString("nickname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(int id) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM userStorage where id=?");
            preparedStatement.setInt(1, id);
            ResultSet results = preparedStatement.executeQuery();
            connection.close();
            if (!results.next()) {
                return null;
            } else {
                return new User(results.getInt("id"), results.getString("login"), results.getString("pass"),
                        results.getString("nickname"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateNickname(int userId, String nickname) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE userstorage SET nickname=? WHERE id=?");
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(1, nickname);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace(); //TODO
        }
    }

    @Override
    public void changePass(int userId, String newPass) {
        Connection connection;
        try {
            connection = source.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE userstorage SET pass=? WHERE id=?");
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(1, newPass);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
