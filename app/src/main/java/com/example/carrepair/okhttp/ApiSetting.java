package com.example.carrepair.okhttp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ApiSetting {

    public String KEY_IP_SERVER = "KEY_IP_SERVER";
    public static String LOGIN_STATUS = "LOGIN_STATUS";
    public static String USER_NAME = "USER_NAME";
    public static String EMAIL = "EMAIL";
    public static String USER_ID = "USER_ID";
    public static String USER_TYPE_ID = "USER_TYPE_ID";

    private SharedPreferences mPreferences;

    public ApiSetting(Context context) {
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //---------------------START KEY_IP_SERVER-------------//
    public String getBASE_URL(String route) {
        return mPreferences.getString(KEY_IP_SERVER, "http://192.168.70.93/api/")+route;
    }

    public void setBASE_URL(String url) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_IP_SERVER, url);
        editor.apply();
    }

    //---------------------PUT SET STRING-------------//
    public String getString(String key) {
        return mPreferences.getString(key,"");
    }

    public void putString(String key,String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //---------------------PUT SET BOOLEAN-------------//
    public boolean getBoolean(String key) {
        return mPreferences.getBoolean(key,false);
    }

    public void putBoolean(String key,boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    //---------------------PUT SET INTEGER-------------//
    public int getInt(String key) {
        return mPreferences.getInt(key,0);
    }

    public void putInt(String key,int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
