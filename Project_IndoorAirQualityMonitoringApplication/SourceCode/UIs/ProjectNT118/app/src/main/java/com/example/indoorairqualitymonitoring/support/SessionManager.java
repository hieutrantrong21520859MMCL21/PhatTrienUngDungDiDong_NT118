package com.example.indoorairqualitymonitoring.support;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.indoorairqualitymonitoring.model.Token;

public class SessionManager
{
    private static final String SHARED_PREFS_NAME = "login_session";
    private static final String TOKEN = "token";
    private static final String EXPIRATION = "expiration";
    private static final String LOGIN = "loginTimeInMillis";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private SharedPreferences shrPrefs;
    private SharedPreferences.Editor editor;
    public SessionManager(Context context)
    {
        shrPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = shrPrefs.edit();
    }
    public void saveSession(Token token, String username, String password)
    {
        editor.putString(TOKEN, token.getAccessToken());
        editor.putInt(EXPIRATION, token.getExpire());
        editor.putLong(LOGIN,System.currentTimeMillis() / 1000);
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.apply();
    }
    public void clearSession()
    {
        editor.putString(TOKEN,null);
        editor.putInt(EXPIRATION,0);
        editor.putLong(LOGIN,0);
        editor.putString(USERNAME,null);
        editor.putString(PASSWORD,null);
        editor.apply();
    }
    public String getToken()
    {
        return shrPrefs.getString(TOKEN,null);
    }
    public int getExpiration()
    {
        return shrPrefs.getInt(EXPIRATION,0);
    }
    public long getTheLatestLoginTime()
    {
        return shrPrefs.getLong(LOGIN,0);
    }
    public String getUsername()
    {
        return shrPrefs.getString(USERNAME,null);
    }
    public String getPassword()
    {
        return shrPrefs.getString(PASSWORD,null);
    }
}