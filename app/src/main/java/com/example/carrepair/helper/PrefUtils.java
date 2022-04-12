package com.example.carrepair.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
    private static PrefUtils instance;

    private static final String IS_ADMIN = "is_admin";
    private static final String IS_LOGIN = "is_login";

    private SharedPreferences mPreferences;

    public static PrefUtils getInstance(Context context) {
        if (instance == null) {
            instance = new PrefUtils(context);
        }
        return instance;
    }

    public PrefUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isAdmin() {
        return mPreferences.getBoolean(IS_ADMIN,false);
    }

    public void setAdmin(boolean admin) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(IS_ADMIN, admin);
        editor.apply();
    }

    public boolean isLogin() {
        return mPreferences.getBoolean(IS_LOGIN,false);
    }

    public void setIsLogin(boolean admin) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(IS_LOGIN, admin);
        editor.apply();
    }
}