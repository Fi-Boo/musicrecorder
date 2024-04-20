package com.cca2.musiclibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.io.IOException;
import java.text.Collator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

public class MusicLibrary {

    ArrayList<User> users = new ArrayList<User>();
    ArrayList<Song> songs = new ArrayList<Song>();
    HashSet<String> subscribedSongs = new HashSet<String>();

    DatabaseController dbc;
    User loggedUser;

    public MusicLibrary() {

        populateSongsList();
        dbc = new DatabaseController();

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

    public boolean checkLogin(String email, String password) {

        List<User> results = dbc.checkUserByLogin(email, password);

        if (results.size() > 0) {
            loggedUser = results.get(0);

            System.out.println("Logged user set to: " + loggedUser.getUsername());
            return true;
        } else {
            return false;
        }
    }

    public String getLoggedUsername() {
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

    public Object getArtists() {

        // https://stackoverflow.com/questions/708698/how-can-i-sort-a-list-alphabetically
        Collection<String> artists = new TreeSet<String>(Collator.getInstance());

        for (Song song : songs) {
            artists.add(song.getArtist());
        }

        return artists;

    }

    public List<Song> queryFor(String title, String year, String artist) {

        List<Song> filteredSongs = songs.stream()
                .filter(song -> title == "" || song.getTitle().contains(title))
                .filter(song -> year == "" || song.getYear().contains(year))
                .filter(song -> artist == "" || song.getArtist().contains(artist))
                .collect(Collectors.toList());

        return filteredSongs;

    }

    public Object getCurrentUser() {
        return loggedUser.getEmail();
    }

    public Object getUsernameByEmail(String loggedUserEmail) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(loggedUserEmail)) {
                return user.getUsername();
            }
        }
        return null;
    }

    public void addToSubscribeList(String subTitle) {

        subscribedSongs.add(subTitle);
    }

}
