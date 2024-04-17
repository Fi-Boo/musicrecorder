package com.cca2.musiclibrary;

import java.util.ArrayList;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

public class MusicLibrary {

    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Song> songs = new ArrayList<Song>();
    User loggedUser;

    public MusicLibrary() {

        populateUserList();
        populateSongsList();

    }

    private void populateSongsList() {

        ObjectMapper mapper = new ObjectMapper();

        try {

            ClassPathResource resource = new ClassPathResource("a2.json");
            // Read JSON file and deserialize it into an ArrayList of MyObject
            SongList songList = mapper.readValue(resource.getInputStream(), SongList.class);
            songs = songList.getSongs();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(songs.size() + " songs added to the list");
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
