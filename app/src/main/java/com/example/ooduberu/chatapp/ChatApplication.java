package com.example.ooduberu.chatapp;

import android.app.Application;

import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatApplication extends Application {
    private static ChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);//this is to enable fire base off line capability
        AppPreference.setUpDefault(this);
        instance = this;

    }

    public static ChatApplication getInstance() {
        return instance;
    }
}
