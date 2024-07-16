package com.example.indoorairqualitymonitoring.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper
{
    private static final String SHARED_PREFS_NAME = "languages";
    private static final String LANGUAGE_CODE = "langCode";
    private static final String SELECTED_LANGUAGE_POSITION = "selectedLangPos";
    private SharedPreferences shrPrefs;
    private SharedPreferences.Editor editor;
    public LocaleHelper(Context context)
    {
        shrPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = shrPrefs.edit();
    }
    // Get which language is in usage
    public String getLanguageCode()
    {
        return shrPrefs.getString(LANGUAGE_CODE,"en");
    }

    // Get index in language dialog
    public int getSelectedLanguagePosition()
    {
        return shrPrefs.getInt(SELECTED_LANGUAGE_POSITION,0);
    }

    // Change language
    public void setLocale(Context context, String langCode)
    {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Put data to shared preferences
        editor.putString(LANGUAGE_CODE, langCode);

        if (langCode.equals("en"))
        {
            editor.putInt(SELECTED_LANGUAGE_POSITION,0);
        }
        else if (langCode.equals("vn"))
        {
            editor.putInt(SELECTED_LANGUAGE_POSITION,1);
        }
        else
        {
            editor.putInt(SELECTED_LANGUAGE_POSITION,2);
        }

        editor.apply();
    }
}