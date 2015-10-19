package ru.mail.track;


import java.util.*;

public class UserStore {

    ArrayList<User> Users = new ArrayList<User>();

    // Вам нужно выбрать, как вы будете хранить ваших пользователей, например в массиве User users[] = new User[100];

    // проверить, есть ли пользователь с таким именем
    // если есть, вернуть true
    boolean isUserExist(String name) {
        for (int i = 0; i < Users.size(); ++i) {
            if (Users.get(i).getName().compareTo(name) == 0) {
                return true;
            }
        }
        return false;
    }


    // Добавить пользователя в хранилище
    void addUser(User user) {
        Users.add(user);
    }

    User getUser(String name, String pass) {
        for (int i = 0; i < Users.size(); ++i) {
            if (Users.get(i).getName().compareTo(name) == 0 && Users.get(i).getPass().compareTo(pass) == 0) {
                return Users.get(i);
            }
        }
        return null;
    }
}
