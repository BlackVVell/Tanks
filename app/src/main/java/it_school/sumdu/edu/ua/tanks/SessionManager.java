package it_school.sumdu.edu.ua.tanks;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "session";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LOGGED_IN = "loggedIn";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(KEY_LOGGED_IN, loggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public void logout() {
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_LOGGED_IN);
        editor.apply();
    }
}

