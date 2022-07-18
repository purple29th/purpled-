package com.example.purpled.myUploads;

public class MyUploadsList {

    private String trackname, description, genre, image, url;

    public MyUploadsList( String trackname, String description, String genre, String image, String url) {
        this.trackname = trackname;
        this.description = description;
        this.genre = genre;
        this.image = image;
        this.url = url;
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
}