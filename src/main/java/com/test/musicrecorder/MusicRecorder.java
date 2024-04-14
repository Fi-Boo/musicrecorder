package com.test.musicrecorder;

public class MusicRecorder {

    String username = "Nebs";
    String password = "CapnKOOKD";

    public boolean validatelogin(String username, String password) {
        if (username.equalsIgnoreCase(this.username) && password.equalsIgnoreCase(this.password)) {
            return true;
        } else {
            return false;
        }
    }

    public Object getUsername() {

        return username;
    }

    public Object getPassword() {

        return password;
    }

}
