package com.example.ooduberu.chatapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    int SPLASH_TIME_OUT = 4000;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreference.isFirstTimeLaunch()) {
                    startActivity(new Intent(SplashScreenActivity.this, WelcomeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //This resolves the memory leak by removing the handler references.
        handler.removeCallbacksAndMessages(null);

    }
}
