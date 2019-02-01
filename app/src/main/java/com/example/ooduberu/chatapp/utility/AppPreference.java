package com.example.ooduberu.chatapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreference {
    private static SharedPreferences sharedPreferences;

     private static final String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";
     private static final String CURRENT_USER_ID = "current_user_id";
     private static final String CURRENT_USERNAME = "current_user_name";
     private static final String CHECK_PENDING_REQUEST_COUNT = "check_pending_request_count";
     private static final String PENDING_REQUESTS_COUNT = "pending_requests_count";


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

    public static void setCheckPendingRequestCount(boolean checkPendingRequestCount){
        if(sharedPreferences != null){
            sharedPreferences.edit().putBoolean(CHECK_PENDING_REQUEST_COUNT,checkPendingRequestCount).apply();
        }
    }

    public static boolean IsPendingRequestCounted(){
        return sharedPreferences != null && sharedPreferences.getBoolean(CHECK_PENDING_REQUEST_COUNT,false);
    }

    public static void setPendingRequestsCount(long count){
        if(sharedPreferences != null){
            sharedPreferences.edit().putLong(PENDING_REQUESTS_COUNT,count).apply();
        }
    }

    public static long getPendingRequestsCount(){
        if(sharedPreferences != null){
            return sharedPreferences.getLong(PENDING_REQUESTS_COUNT,0);
        }
        return 0;
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

    public static void setCurrentUserName(String userName){
        if (sharedPreferences != null){
            sharedPreferences.edit().putString(CURRENT_USERNAME, userName).apply();
        }
    }

    public static String getCurrentUserName(){
        if (sharedPreferences != null){
            return sharedPreferences.getString(CURRENT_USERNAME,"");
        }
        return null;

    }



}
