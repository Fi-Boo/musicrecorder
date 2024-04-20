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
    }

    // private void populateSongsList() {

    // ObjectMapper mapper = new ObjectMapper();

    // try {

    // ClassPathResource resource = new ClassPathResource("a2.json");
    // // Read JSON file and deserialize it into an ArrayList of MyObject
    // SongList songList = mapper.readValue(resource.getInputStream(),
    // SongList.class);
    // songs = songList.getSongs();

    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    // System.out.println(songs.size() + " songs added to the list");
    // }

    public boolean checkLoginCredentials(String email, String password) {

        dbc = new DatabaseController("login");

        List<User> results = dbc.getDatabaseListByEmail(email);

        if (results.size() > 0 && results.get(0).getPassword().equals(password)) {

            loggedUser = results.get(0);
            System.out.println("Logged user set to: " + loggedUser.getUsername());
            return true;

        } else {
            return false;
        }
    }

    public boolean checkEmailExists(String email) {

        dbc = new DatabaseController("login");

        List<User> results = dbc.getDatabaseListByEmail(email);

        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getLoggedUsername() {
        return loggedUser.getUsername();
    }

    public void addUser(String email, String username, String password) {

        dbc = new DatabaseController("login");

        dbc.addUserEntry(email, username, password);
    }

    public void logout() {

        loggedUser = null;
    }

    public Object getArtists() {

        // https://stackoverflow.com/questions/708698/how-can-i-sort-a-list-alphabetically
        Collection<String> artists = new TreeSet<String>(Collator.getInstance());

        List<Song> songs = queryFor("", "", "");

        for (Song song : songs) {
            artists.add(song.getArtist());
        }

        return artists;

    }

    // public List<Song> queryFor(String title, String year, String artist) {

    // List<Song> filteredSongs = songs.stream()
    // .filter(song -> title == "" || song.getTitle().contains(title))
    // .filter(song -> year == "" || song.getYear().contains(year))
    // .filter(song -> artist == "" || song.getArtist().contains(artist))
    // .collect(Collectors.toList());

    // return filteredSongs;

    // }

    public List<Song> queryFor(String title, String year, String artist) {

        dbc = new DatabaseController("music");

        List<Song> scanResults = dbc.getSongs(title, year, artist);

        return scanResults;
    }

    public Object getCurrentUser() {
        return loggedUser.getEmail();
    }

    public void addToSubscribeList(String subTitle) {

        subscribedSongs.add(subTitle);
    }

}
