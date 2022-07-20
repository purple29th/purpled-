package com.example.purpled.Timeline;

public class TimelineList {

    private String trackname, description, genre, image, url, username, timestamp, userImg, key;
    private int likes;

    public TimelineList( String trackname, String description, String genre, String image, String url, String username, int likes
    ,String timestamp, String userImg, String key) {
        this.trackname = trackname;
        this.description = description;
        this.genre = genre;
        this.image = image;
        this.url = url;
        this.username = username;
        this.likes = likes;
        this.timestamp = timestamp;
        this.userImg = userImg;
        this.key = key;

    }


    public String getTrackname() {
        return trackname;
    }

    public String getDescription() {
        return description;
    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public int getLikes() {
        return likes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserImg() {
        return userImg;
    }

    public String getKey() {
        return key;
    }
}