package com.example.purpled.playlist;

public class playlistList {

    private String trackname, artist, image, url;

    public playlistList( String trackname, String artist, String image, String url) {
        this.trackname = trackname;
        this.artist = artist;
        this.image = image;
        this.url = url;
    }


    public String getTrackname() {
        return trackname;
    }

    public String getArtist() {
        return artist;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}