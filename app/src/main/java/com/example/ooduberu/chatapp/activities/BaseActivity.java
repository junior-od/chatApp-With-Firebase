package com.example.ooduberu.chatapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.example.ooduberu.chatapp.dialogs.ProgressDialogController;
import com.example.ooduberu.chatapp.interfaces.Listener;

public class BaseActivity extends AppCompatActivity implements Listener {
    ProgressDialogController progressDialogController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialogController = new ProgressDialogController(getSupportFragmentManager(),"please wait..");
    }

    @Override
    public void showProgressLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!progressDialogController.isProgressVisible()) {
                    progressDialogController.startProgress();
                }
            }});
    }

    @Override
    public void hideProgressLoader() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialogController.finishProgress();
            }});
    }
}
