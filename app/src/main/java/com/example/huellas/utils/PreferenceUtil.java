package com.example.huellas.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    public PreferenceUtil() {
    }

    public static boolean saveUser(String email, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.USER, email);
        editor.apply();
        return true;
    }

    public static String getUser(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.USER, null);
    }

    public static boolean saveUserId(int userId, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.USER_ID, userId);
        editor.apply();
        return true;
    }

    public static int getUserId(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(Constants.USER_ID, 0);
    }


}