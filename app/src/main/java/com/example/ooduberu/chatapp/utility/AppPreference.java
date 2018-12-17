package com.example.ooduberu.chatapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreference {
    private static SharedPreferences sharedPreferences;

     private static final String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";
     private static final String CURRENT_USER_ID = "current_user_id";


    public static SharedPreferences setUpDefault(Context context) {
        if(sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    public static void setIsFirstTimeLaunch(boolean isFirstTimeLaunch){
        if(sharedPreferences!= null){
            sharedPreferences.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch).apply();
        }
    }

    public static boolean isFirstTimeLaunch(){
        return sharedPreferences != null && sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    public static void setCurrentUserId(String userId){
        if (sharedPreferences != null){
            sharedPreferences.edit().putString(CURRENT_USER_ID, userId).apply();
        }
    }

    public static String getCurrentUserId(){
        if (sharedPreferences != null){
            return sharedPreferences.getString(CURRENT_USER_ID,"");
        }
        return null;

    }

}
