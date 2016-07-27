package com.development.transejecutivosdrivers.misc;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by william.montiel on 27/07/2016.
 */
public class CacheManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private String preferName = null;
    // All Shared Preferences Keys
    private String key = null;

    /**
     * Set configuration data for store
     * @Context context
     * @String preferName
     * @String key
     */
    public CacheManager(Context context, String preferName, String key){
        this._context = context;
        this.preferName = preferName;
        this.key = key;
        pref = _context.getSharedPreferences(preferName, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Store data
     */
    public void setData(String key, String data){
        // Storing login value as TRUE
        editor.putBoolean(this.key, true);
        // Storing user in pref
        editor.putString(key, data);
        // commit changes
        editor.commit();
    }

    public void setBoolean(Boolean data) {
        editor.putBoolean(this.key, data);
    }

    /**
     * Check if there is data stored
     * */
    public boolean checkData(){
        // Check login status
        if(!this.isDataLoaded()){
            return false;
        }
        return true;
    }

    /**
     * Get stored data
     * */
    public String getData(String key){
        // return data
        return pref.getString(key, null);
    }

    /**
     * Clear stored data
     * */
    public void cleanData(){
        // Clearing all user data from Shared Preferences
        if(this.isDataLoaded()){
            editor.clear();
            editor.commit();
        }
    }

    public boolean isDataLoaded(){
        return pref.getBoolean(this.key, false);
    }
}
