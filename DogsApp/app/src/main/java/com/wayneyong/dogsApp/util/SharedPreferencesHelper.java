package com.wayneyong.dogsApp.util;


/*This class help us to store and retrieve data from sharedPreferences*/

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SharedPreferencesHelper {

    /*
     * In software engineering, the singleton pattern is a software design pattern that restricts the instantiation of a class to one "single" instance.
     * This is useful when exactly one object is needed to coordinate actions across the system. The term comes from the mathematical concept of a singleton.
     * */

    private static final String PREF_TIME = "Pref time"; //preference to store time, caching time that we need
    private static SharedPreferencesHelper instance;
    /*
     * Declare instance here, so that we can have SharedPreference as singleton, we don't want multiple instances accessing the same preference at the same time
     * Imagine if an instance of SPHelper want to read preference while another instance want to write that exact same preference at the same time, it will cause
     * inconsistencies
     * */
    private SharedPreferences prefs; //object that allow us to read and write from the data

    //set to be private, we don't want any other component of app to be able instance SPHelper, make sure only variable/object in our code
    private SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    //caching time
    public void saveUpdateTime(long time) {
        //last time we retrieved data from api
        prefs.edit().putLong(PREF_TIME, time).apply();
    }

    public long getUpdateTime() {
        return prefs.getLong(PREF_TIME, 0);
    }

    public String getCacheDuration() {
        //settings.xml
        return prefs.getString("pref_cache_duration", "");
    }
}
