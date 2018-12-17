package com.example.ooduberu.chatapp;

import android.app.Application;

import com.example.ooduberu.chatapp.utility.AppPreference;

public class ChatApplication extends Application {
    private static ChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreference.setUpDefault(this);
        instance = this;

    }

    public static ChatApplication getInstance() {
        return instance;
    }
}
