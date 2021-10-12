/*
 * Copyright © 2021. Development by Mehmet Emre KOBAK
 * Contact: kobakmehmetemre@gmail.com
 *
 */

package com.memrekobak.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPref {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Session";
    public static final String KEY_USER = "user";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BROKER = "broker";

    public SharedPref(Context context) {

        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String user, String password, String broker) {
        editor.putString(KEY_USER, user);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_BROKER, broker);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {

        HashMap user = new HashMap();

        user.put(KEY_USER, sharedPreferences.getString(KEY_USER, null));

        user.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));

        user.put(KEY_BROKER, sharedPreferences.getString(KEY_BROKER, null));

        return user;
    }

    public void clearUser() {
        editor.clear();
        editor.commit();
    }


}

