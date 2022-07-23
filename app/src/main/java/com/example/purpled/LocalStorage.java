package com.example.purpled;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorage {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    String token;
    String username;
    String auth_type;
    String uid;
    String trackTitle;
    String trackArtist;
    String trackImage;
    String trackUrl;
    String tracks;
    String oneTimeState = "yes";
    String oneTimeState2 = "yes";
    String myGenre;

    public LocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getOneTimeState() {
        oneTimeState = sharedPreferences.getString("ONETIMESTATE", "");
        return oneTimeState;
    }

    public String getOneTimeState2() {
        oneTimeState2 = sharedPreferences.getString("ONETIMESTATE2", "");
        return oneTimeState2;
    }

    public void setOneTimeState(String oneTimeState) {
        editor.putString("ONETIMESTATE", oneTimeState);
        editor.commit();
        this.oneTimeState = oneTimeState;
    }

    public void setOneTimeState2(String oneTimeState2) {
        editor.putString("ONETIMESTATE2", oneTimeState2);
        editor.commit();
        this.oneTimeState2 = oneTimeState2;
    }

    public String getMyGenre() {
        myGenre = sharedPreferences.getString("MYGENRE", "");
        return myGenre;
    }

    public void setMyGenre(String myGenre) {
        editor.putString("MYGENRE", myGenre);
        editor.commit();
        this.myGenre = myGenre;
    }

    public String getUid() {
        uid = sharedPreferences.getString("UID", "");
        return uid;
    }

    public void setUid(String uid) {
        editor.putString("UID", uid);
        editor.commit();
        this.uid = uid;
    }

    public String getToken() {
        token = sharedPreferences.getString("TOKEN", "");
        return token;
    }

    public void setToken(String token) {
        editor.putString("TOKEN", token);
        editor.commit();
        this.token = token;
    }

    public String getUserName() {
        username = sharedPreferences.getString("EMAIL", "");
        return username;
    }

    public void setUserName(String username) {
        editor.putString("EMAIL", username);
        editor.commit();
        this.username = username;
    }

    public String getAuth_type() {
        auth_type = sharedPreferences.getString("AUTH_TYPE", "");
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        editor.putString("AUTH_TYPE", auth_type);
        editor.commit();
        this.auth_type = auth_type;
    }

    //track details storage
    public String getTrackTitle() {
        trackTitle = sharedPreferences.getString("TRACKTITLE", "");
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        editor.putString("TRACKTITLE", trackTitle);
        editor.commit();
        this.trackTitle = trackTitle;
    }

    public String getTrackArtist() {
        trackArtist = sharedPreferences.getString("TRACKARTIST", "");
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        editor.putString("TRACKARTIST", trackArtist);
        editor.commit();
        this.trackArtist = trackArtist;
    }

    public String getTrackImage() {
        trackImage = sharedPreferences.getString("TRACKIMAGE", "");
        return trackImage;
    }

    public void setTrackImage(String trackImage) {
        editor.putString("TRACKIMAGE", trackImage);
        editor.commit();
        this.trackImage = trackImage;
    }

    public String getTrackUrl() {
        trackUrl = sharedPreferences.getString("TRACKURL", "");
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        editor.putString("TRACKURL", trackUrl);
        editor.commit();
        this.trackUrl = trackUrl;
    }

    public String getTracks() {
        tracks = sharedPreferences.getString("TRACKS", "");
        return tracks;
    }

    public void setTracks(String tracks) {
        editor.putString("TRACKS", tracks);
        editor.commit();
        this.tracks = tracks;
    }
}
