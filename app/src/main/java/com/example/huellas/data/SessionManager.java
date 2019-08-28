package com.example.huellas.data;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {
    private SharedPreferences prefs;
    public Context context;
    public SharedPreferences.Editor editor;
    private static final String PREF_NAME = "LOGIN";
    public static final String USERNAME = "USERNAME";
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }
    public void setUsername(String username) {
        prefs.edit().putString(USERNAME, username).commit();
    }

    public String getUsername() {
        String username = prefs.getString(USERNAME,"");
        return username;
    }

    public  void logout( ){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
