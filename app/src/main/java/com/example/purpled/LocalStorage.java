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

    public LocalStorage(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
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
}
