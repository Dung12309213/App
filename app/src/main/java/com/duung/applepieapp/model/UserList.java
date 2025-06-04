package com.duung.applepieapp.model;


import java.util.ArrayList;

public class UserList {
    private ArrayList<User> users;

    public UserList() {
        users = new ArrayList<>();
        generateSampleUsers();
    }

    private void generateSampleUsers() {
        users.add(new User("admin", "123456"));
        users.add(new User("duung", "applepie"));
        users.add(new User("test", "test123"));
    }

    public boolean isValidUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}

