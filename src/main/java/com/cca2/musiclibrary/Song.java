package com.cca2.musiclibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Song {

    private String title;
    private String artist;
    private String year;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("img_url")
    private String imgUrl;

    public Song() {
    }

    public Song(String title, String artist, String year, String webUrl, String imgUrl) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.webUrl = webUrl;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Song{ title=" + title +
                ", artist=" + artist +
                ", year=" + year +
                ", webUrl=" + webUrl +
                ", imgUrl=" + imgUrl +
                "}\n";
    }
}
