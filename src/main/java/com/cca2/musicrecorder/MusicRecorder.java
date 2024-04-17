package com.cca2.musicrecorder;

import java.util.ArrayList;

public class MusicRecorder {

    ArrayList<User> users = new ArrayList<User>();
    User loggedUser;

    public MusicRecorder() {

        populateUserList();
    }

    private void populateUserList() {

        String name = "phibui";
        String emailsuffix = "@student.rmit.edu.au";
        String password = "012345678901234";

        for (int i = 0; i < 10; i++) {
            users.add(new User(name + i, name + i + emailsuffix, password.substring(0 + i, 6 + i)));
        }
    }

    public boolean checkLogin(String email, String password) {

        boolean userFound = false;

        for (User user : users) {

            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {

                loggedUser = user;
                userFound = true;
            }
        }
        return userFound;
    }

    public String getUsername() {
        return loggedUser.getUsername();
    }

    public void addUser(String email, String username, String password) {

        users.add(new User(username, email, password));
    }

    public boolean checkEmailExists(String email) {

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }

    public void logout() {

        loggedUser = null;
    }

}
