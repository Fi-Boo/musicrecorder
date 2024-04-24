package com.cca2.musiclibrary;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;

public class MusicLibrary {

    DatabaseController dbc;
    User loggedUser;
    S3Controller s3c;

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

        return dbc.getSubscriptionsByEmail(loggedUser.getEmail());
    }

    /*
     * 
     * 
     */
    public void removeFromSubscribeList(String songTitle) {

        dbc = new DatabaseController("subscriptions");

        dbc.removeFromSubscribeList(loggedUser.getEmail(), songTitle);

    }

    public List<String> getArtistImg(List<Song> subscriptionsByEmail) {

        List<String> list = new ArrayList<String>();

        s3c = new S3Controller();
        String bucketName = "cca2.artists";

        for (Song song : subscriptionsByEmail) {

            String imageName = song.getImgUrl().substring(87, song.getImgUrl().length());
            try {
                list.add(convertIStoBase64(s3c.getStream(bucketName, imageName)));
            } catch (IOException e) {

            }
        }

        return list;
    }

    private String convertIStoBase64(InputStream inputStream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] imageBytes = outputStream.toByteArray();

        // Encode the byte array to Base64
        String base64Encoded = Base64.getEncoder().encodeToString(imageBytes);

        // Return the Base64-encoded string
        return base64Encoded;
    }

}
