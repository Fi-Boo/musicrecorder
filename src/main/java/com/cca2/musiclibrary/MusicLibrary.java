package com.cca2.musiclibrary;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.text.Collator;

public class MusicLibrary {

    DatabaseController dbc;
    User loggedUser;

    /*
     * 
     * 
     */
    public MusicLibrary() {
    }

    /*
     * 
     * 
     */
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

    /*
     * 
     * 
     */
    public boolean checkEmailExists(String email) {

        dbc = new DatabaseController("login");

        List<User> results = dbc.getDatabaseListByEmail(email.toLowerCase());

        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 
     * 
     */
    public String getLoggedUsername() {
        return loggedUser.getUsername();
    }

    /*
     * 
     * 
     */
    public void addUser(String email, String username, String password) {

        dbc = new DatabaseController("login");

        dbc.addUserEntry(email.toLowerCase(), username, password);
    }

    /*
     * 
     * 
     */
    public void logout() {

        loggedUser = null;
    }

    /*
     * 
     * 
     */
    public Object getArtists() {

        // https://stackoverflow.com/questions/708698/how-can-i-sort-a-list-alphabetically
        Collection<String> artists = new TreeSet<String>(Collator.getInstance());

        List<Song> songs = queryFor("", "", "");

        for (Song song : songs) {
            artists.add(song.getArtist());
        }

        return artists;

    }

    /*
     * 
     * 
     */
    public List<Song> queryFor(String title, String year, String artist) {

        dbc = new DatabaseController("music");

        List<Song> scanResults = dbc.getSongs(StringEditor.capitalize(title), year, artist);

        return scanResults;
    }

    /*
     * 
     * 
     */
    public Object getCurrentUser() {
        return loggedUser.getEmail();
    }

    /*
     * 
     * 
     */
    public void addToSubscribeList(String subTitle) {

        dbc = new DatabaseController("subscriptions");

        dbc.addToSubscriptions(loggedUser.getEmail(), subTitle);
    }

    /*
     * 
     * 
     */
    public List<Song> getSubscriptionsByEmail() {

        dbc = new DatabaseController("subscriptions");

        List<Song> songs = dbc.getSubscriptionsByEmail(loggedUser.getEmail());

        return songs;

    }

    /*
     * 
     * 
     */
    public void removeFromSubscribeList(String songTitle) {

        dbc = new DatabaseController("subscriptions");

        dbc.removeFromSubscribeList(loggedUser.getEmail(), songTitle);

    }

}
