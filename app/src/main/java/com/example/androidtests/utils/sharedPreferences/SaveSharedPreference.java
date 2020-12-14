package com.example.androidtests.utils.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.androidtests.models.User;
import com.example.androidtests.models.UserChallenge;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import static com.example.androidtests.utils.sharedPreferences.PreferencesUtility.LOGGED_IN_PREF;

public class SaveSharedPreference {
    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.commit();
    }

    public static void setLogedInUser(Context context, User user) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("current_user", json);
        editor.commit();
    }

    public static User getLogedInUser(Context context) {
        Gson gson = new Gson();
        String json = getPreferences(context).getString("current_user","");
        return gson.fromJson(json, User.class);
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    public static String getLocale(Context context) {
        return getPreferences(context).getString("locale", "en");
    }

    public static void setLocale(Context context, String localeChoice) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString("locale", localeChoice);
        editor.commit();
    }
}
