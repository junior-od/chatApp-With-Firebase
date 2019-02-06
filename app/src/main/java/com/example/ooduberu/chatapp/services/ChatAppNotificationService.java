package com.example.ooduberu.chatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ooduberu.chatapp.model.ActivitiesBody;
import com.example.ooduberu.chatapp.model.FollowNotificationBody;
import com.example.ooduberu.chatapp.model.User;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatAppNotificationService extends IntentService {
    private static final String TAG = ChatAppNotificationService.class.getSimpleName();

    private static final String FOLLOW_UNLOCKED = "follow_unlocked";
    private static final String FOLLOW_LOCKED = "follow_locked";
    private static final String ACCEPT_FOLLOW = "accept_follow_request";
    private static final String CANCEL_REQUEST = "cancel_request";
    private static final String CANCEL_ACCEPT_REQUEST = "cancel_accept_request";
    private static final String CANCEL_FOLLOW_NOTIFICATION = "cancel_follow_notification";

    DatabaseReference followNotifications;
    DatabaseReference followRequestNotifications;
    DatabaseReference acceptFollowRequestNotifications;

    DatabaseReference activities;


    String action_type;
    String user_id;
    String receiver_id;

    public ChatAppNotificationService() {
        super("ChatAppNotificationService");
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

            case CANCEL_FOLLOW_NOTIFICATION:
                cancelFollowNotification(user_id,receiver_id);
                break;

        }

    }

    private void followUnlockedAccount(final String user_id, final String receiver_id){
        activities = FirebaseDatabase.getInstance().getReference().child("activities");
        followNotifications = FirebaseDatabase.getInstance().getReference().child("followNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("just followed you !!!");
        followNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
//                    String user_id;
//                    String post_id;
//                    String type;
//                    String message;
//                    String time;
                    ActivitiesBody activitiesBody = new ActivitiesBody();
                    activitiesBody.setUser_id(user_id);
                    activitiesBody.setType("new follower");
                    activitiesBody.setPost_id("");
                    activitiesBody.setTime("");
                    activitiesBody.setMessage("started following you");
                    activities.child(receiver_id).push().setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"sent sucessfully");
                            }else{
                                Log.i(TAG,task.getException().getMessage());
                            }
                        }
                    });

                }else{
                    Log.i(TAG,task.getException().getMessage());

                }
            }
        });
    }

    private void followLockedAccount(final String user_id, final String receiver_id){
        activities = FirebaseDatabase.getInstance().getReference().child("activities");
        followRequestNotifications = FirebaseDatabase.getInstance().getReference().child("followRequestNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("just followed you back !!!");
        followRequestNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    ActivitiesBody activitiesBody = new ActivitiesBody();
                    activitiesBody.setUser_id(user_id);
                    activitiesBody.setType("follower request");
                    activitiesBody.setPost_id("");
                    activitiesBody.setTime("");
                    activitiesBody.setMessage("requested to follow you");
                    activities.child(receiver_id).push().setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"sent sucessful");
                            }else{
                                Log.i(TAG,task.getException().getMessage());
                            }
                        }
                    });

                }else{
                    Log.i(TAG,task.getException().getMessage());

                }

            }
        });
    }

    private void acceptFollowRequest(final String user_id, final String receiver_id){
        activities = FirebaseDatabase.getInstance().getReference().child("activities");
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("accepted request");
        acceptFollowRequestNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    ActivitiesBody activitiesBody = new ActivitiesBody();
                    activitiesBody.setUser_id(user_id);
                    activitiesBody.setType("new follower");
                    activitiesBody.setPost_id("");
                    activitiesBody.setTime("");
                    activitiesBody.setMessage("started following you");

                    activities.child(receiver_id).push().setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"sent message sucessful");
                            }else{
                                Log.i(TAG,task.getException().getMessage());
                            }
                        }
                    });
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

    private void cancelFollowNotification(String user_id,String receiver_id){
        followNotifications = FirebaseDatabase.getInstance().getReference().child("followNotification");
        followNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"cancel follow notification sucessfully");
                }else{
                    Log.i(TAG,task.getException().getMessage());
                }
            }
        });

    }

}
