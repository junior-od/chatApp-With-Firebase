package com.example.ooduberu.chatapp.activities;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.example.ooduberu.chatapp.dialogs.ProgressDialogController;
import com.example.ooduberu.chatapp.interfaces.Listener;
import com.example.ooduberu.chatapp.services.ChatAppService;

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

    @Override
    public void sendNotification(String actionType, String user_id, String receiver_id) {
        Intent intent = new Intent(this, ChatAppService.class);
        intent.putExtra("actionType",actionType);
        intent.putExtra("userId",user_id);
        intent.putExtra("receiverId",receiver_id);
        startService(intent);
    }
}
