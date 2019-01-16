package com.example.ooduberu.chatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ooduberu.chatapp.model.FollowNotificationBody;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ChatAppService extends IntentService {
    private static final String TAG = ChatAppService.class.getSimpleName();

    private static final String FOLLOW_UNLOCKED = "follow_unlocked";
    private static final String FOLLOW_LOCKED = "follow_locked";
    private static final String ACCEPT_FOLLOW = "accept_follow_request";
    private static final String CANCEL_REQUEST = "cancel_request";
    private static final String CANCEL_ACCEPT_REQUEST = "cancel_accept_request";

    DatabaseReference followNotifications;
    DatabaseReference followRequestNotifications;
    DatabaseReference acceptFollowRequestNotifications;

    String action_type;
    String user_id;
    String receiver_id;


    public ChatAppService() {
        super("ChatAppService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        action_type = intent.getExtras().getString("actionType");
        user_id = intent.getExtras().getString("userId");
        receiver_id = intent.getExtras().getString("receiverId");

        switch (action_type){
            case FOLLOW_UNLOCKED:
                followUnlockedAccount(user_id,receiver_id);
                break;

            case FOLLOW_LOCKED:
                followLockedAccount(user_id,receiver_id);
                break;

            case ACCEPT_FOLLOW:
                acceptFollowRequest(user_id,receiver_id);
                break;

            case CANCEL_REQUEST:
                cancelFollowRequest(user_id,receiver_id);
                break;

            case CANCEL_ACCEPT_REQUEST:
                removeAcceptRequest(user_id,receiver_id);
                break;

        }

    }

    private void followUnlockedAccount(String user_id, String receiver_id){
        followNotifications = FirebaseDatabase.getInstance().getReference().child("followNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("just followed you !!!");
        followNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"sent sucessfully");
                }else{
                    Log.i(TAG,task.getException().getMessage());

                }
            }
        });
    }

    private void followLockedAccount(String user_id, String receiver_id){
        followRequestNotifications = FirebaseDatabase.getInstance().getReference().child("followRequestNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("just followed you back !!!");
        followRequestNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"sent sucessful");
                }else{
                    Log.i(TAG,task.getException().getMessage());
                }
            }
        });
    }

    private void acceptFollowRequest(String user_id,String receiver_id){
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("accepted request");
        acceptFollowRequestNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"sent message sucessful");
                }else{
                    Log.i(TAG,task.getException().getMessage());
                }
            }
        });

    }

    private void cancelFollowRequest(String user_id,String receiver_id){
        followRequestNotifications = FirebaseDatabase.getInstance().getReference().child("followRequestNotification");
        followRequestNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"canceled request notification sucessfully");
                }else{
                    Log.i(TAG,task.getException().getMessage());
                }
            }
        });
    }

    private void removeAcceptRequest(String user_id, String receiver_id){
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");
        acceptFollowRequestNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"cancel accept request sucessfully");
                }else{
                    Log.i(TAG,task.getException().getMessage());
                }
            }
        });



    }




}
