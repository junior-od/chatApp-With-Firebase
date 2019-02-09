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
import com.example.ooduberu.chatapp.utility.TimeDateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class ChatAppNotificationService extends IntentService {
    private static final String TAG = ChatAppNotificationService.class.getSimpleName();

    private static final String FOLLOW_UNLOCKED = "follow_unlocked";
    private static final String FOLLOW_LOCKED = "follow_locked";
    private static final String ACCEPT_FOLLOW = "accept_follow_request";
    private static final String CANCEL_REQUEST = "cancel_request";
    private static final String CANCEL_ACCEPT_REQUEST = "cancel_accept_request";
    private static final String CANCEL_FOLLOW_NOTIFICATION = "cancel_follow_notification";



    DatabaseReference followersTable;
    DatabaseReference followingTable;

    DatabaseReference followNotifications;
    DatabaseReference followRequestNotifications;
    DatabaseReference acceptFollowRequestNotifications;

    DatabaseReference activities;
    DatabaseReference activitiesTemp;
    DatabaseReference activitiesTemp2;



    String action_type;
    String user_id;
    String receiver_id;
    String activity_id;

    public ChatAppNotificationService() {
        super("ChatAppNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        action_type = intent.getExtras().getString("actionType");
        user_id = intent.getExtras().getString("userId");
        receiver_id = intent.getExtras().getString("receiverId");
        activity_id = intent.getExtras().getString("activityId");

        switch (action_type){
            case FOLLOW_UNLOCKED:
                followUnlockedAccount(user_id,receiver_id);
                break;

            case FOLLOW_LOCKED:
                followLockedAccount(user_id,receiver_id);
                break;

            case ACCEPT_FOLLOW:
                acceptFollowRequest(user_id,receiver_id,activity_id);
                break;

            case CANCEL_REQUEST:
                cancelFollowRequest(user_id,receiver_id,activity_id);
                break;

            case CANCEL_ACCEPT_REQUEST:
                removeAcceptRequest(user_id,receiver_id,activity_id);
                break;

            case CANCEL_FOLLOW_NOTIFICATION:
                cancelFollowNotification(user_id,receiver_id,activity_id);
                break;

        }

    }

    private void followUnlockedAccount(final String user_id, final String receiver_id){
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        activities = FirebaseDatabase.getInstance().getReference().child("activities").child(receiver_id).push();
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
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    try {
                        Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
                        long timeInMilliseconds = mDate.getTime();
                      //  System.out.println("Date in milli :: " + timeInMilliseconds);
                        activitiesBody.setTime(""+timeInMilliseconds);
                        //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    activitiesBody.setMessage("started following you");

                    final String activityKey = activities.getKey();
                    activities.setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followersTable.child(receiver_id).child("accepted").child(user_id).child("activity_id").setValue(activityKey).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                }else{
                    Log.i(TAG,task.getException().getMessage());

                }
            }
        });
    }

    private void followLockedAccount(final String user_id, final String receiver_id){
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        activities = FirebaseDatabase.getInstance().getReference().child("activities").child(receiver_id).push();
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
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    try {
                        Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
                        long timeInMilliseconds = mDate.getTime();
                        //  System.out.println("Date in milli :: " + timeInMilliseconds);
                        activitiesBody.setTime(""+timeInMilliseconds);
                        //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    activitiesBody.setMessage("requested to follow you");
                    final String activityKey = activities.getKey();
                    activities.setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followersTable.child(receiver_id).child("pending").child(user_id).child("activity_id").setValue(activityKey).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                }else{
                    Log.i(TAG,task.getException().getMessage());

                }

            }
        });
    }

    private void acceptFollowRequest(final String user_id, final String receiver_id, String activity_id){
        cancelFollowRequest(receiver_id,user_id,activity_id);
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        activities = FirebaseDatabase.getInstance().getReference().child("activities").child(user_id).push();
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");
        FollowNotificationBody fnb = new FollowNotificationBody();
        fnb.setFrom(user_id);
        fnb.setMessage("accepted request");
        fnb.setActivity_id(acceptFollowActivity(user_id,receiver_id));
        acceptFollowRequestNotifications.child(receiver_id).child(user_id).push().setValue(fnb).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    ActivitiesBody activitiesBody = new ActivitiesBody();
                    activitiesBody.setUser_id(receiver_id);
                    activitiesBody.setType("new follower");
                    activitiesBody.setPost_id("");
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                    try {
                        Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
                        long timeInMilliseconds = mDate.getTime();
                        //  System.out.println("Date in milli :: " + timeInMilliseconds);
                        activitiesBody.setTime(""+timeInMilliseconds);
                        //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    activitiesBody.setMessage("started following you");
                    final String activityKey = activities.getKey();

                    activities.setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                               // Log.i(TAG,"sent message sucessful");
                                followersTable.child(user_id).child("accepted").child(receiver_id).child("activity_id").setValue(activityKey).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                }else{
                    Log.i(TAG,task.getException().getMessage());

                }



            }
        });

    }

    private String acceptFollowActivity(final String user_id, final  String receiver_id){
        activitiesTemp2 = FirebaseDatabase.getInstance().getReference().child("activities").child(receiver_id).push();
        ActivitiesBody activitiesBody = new ActivitiesBody();
        activitiesBody.setUser_id(user_id);
        activitiesBody.setType("accepted request");
        activitiesBody.setPost_id("");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
            long timeInMilliseconds = mDate.getTime();
            //  System.out.println("Date in milli :: " + timeInMilliseconds);
            activitiesBody.setTime(""+timeInMilliseconds);
            //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        activitiesBody.setMessage("accepted your follow request");
        final String activityKey = activitiesTemp2.getKey();
        activitiesTemp2.setValue(activitiesBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i(TAG,"done done done");
                    //return activityKey

                }else{
                    Log.i(TAG,task.getException().getMessage());

                }
            }
        });
        return activityKey;
    }

    private void cancelFollowRequest(String user_id, final String receiver_id, final String activity_id){
        activitiesTemp = FirebaseDatabase.getInstance().getReference().child("activities");
        followRequestNotifications = FirebaseDatabase.getInstance().getReference().child("followRequestNotification");
        followRequestNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    activitiesTemp.child(receiver_id).child(activity_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"canceled request notification sucessfully");
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

    private void removeAcceptRequest(String user_id, final String receiver_id, final String activity_id){
        Log.i(TAG,activity_id);
        activitiesTemp2 = FirebaseDatabase.getInstance().getReference().child("activities");
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");
        acceptFollowRequestNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    activitiesTemp2.child(receiver_id).child(activity_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"cancel accept request sucessfully");

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

    private void cancelFollowNotification(String user_id, final String receiver_id, final String activity_id){
        activities = FirebaseDatabase.getInstance().getReference().child("activities");
        followNotifications = FirebaseDatabase.getInstance().getReference().child("followNotification");
        followNotifications.child(receiver_id).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    activities.child(receiver_id).child(activity_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i(TAG,"cancel follow notification sucessfully");

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

}
