package com.development.transejecutivosdrivers.misc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.development.transejecutivosdrivers.LoginActivity;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by developer on 3/25/16.
 */
public class UserSessionManager {
    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFER_NAME = "TransEjecutivosPref";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    // Constructor
    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(User user){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing user in pref
        editor.putInt(JsonKeys.USER_ID, user.getIdUser());
        editor.putString(JsonKeys.USER_NAME, user.getName());
        editor.putString(JsonKeys.USER_LASTNAME, user.getLastName());
        editor.putString(JsonKeys.USER_USERNAME, user.getUsername());
        editor.putString(JsonKeys.USER_EMAIL1, user.getEmail1());
        editor.putString(JsonKeys.USER_EMAIL2, user.getEmail2());
        editor.putString(JsonKeys.USER_PHONE1, user.getPhone1());
        editor.putString(JsonKeys.USER_PHONE2, user.getPhone2());
        editor.putString(JsonKeys.USER_CODE, user.getCode());
        editor.putString(JsonKeys.USER_APIKEY, user.getApikey());

        // commit changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isUserLoggedIn()){

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }



    /**
     * Get stored session data
     * */
    public User getUserDetails(){
        User user = new User();

        user.setIdUser(pref.getInt(JsonKeys.USER_ID, 0));
        user.setName(pref.getString(JsonKeys.USER_NAME, null));
        user.setLastName(pref.getString(JsonKeys.USER_LASTNAME, null));
        user.setUsername(pref.getString(JsonKeys.USER_USERNAME, null));
        user.setEmail1(pref.getString(JsonKeys.USER_EMAIL1, null));
        user.setEmail2(pref.getString(JsonKeys.USER_EMAIL2, null));
        user.setPhone1(pref.getString(JsonKeys.USER_PHONE1, null));
        user.setPhone2(pref.getString(JsonKeys.USER_PHONE2, null));
        user.setCode(pref.getString(JsonKeys.USER_CODE, null));
        user.setApikey(pref.getString(JsonKeys.USER_APIKEY, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
