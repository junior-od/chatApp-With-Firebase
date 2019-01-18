package com.example.ooduberu.chatapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ooduberu.chatapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatAppFetchFollowersAndFollowingService extends IntentService {
    private static final String TAG = ChatAppFetchFollowersAndFollowingService.class.getSimpleName();

    private static final String GET_FOLLOWERS_DETAILS = "get_followers_details";
    private static final String GET_FOLLOWING_DETAILS = "get_following_details";

    String type;
    String user_id;

    DatabaseReference userTable;
    DatabaseReference followersTable;
    DatabaseReference followingTable;

    ArrayList<User> followers_list = new ArrayList<>();
    ArrayList<User> following_list = new ArrayList<>();

    public ChatAppFetchFollowersAndFollowingService() {
        super("ChatAppFetchFollowersAndFollowingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        type = intent.getExtras().getString("type");
        user_id = intent.getExtras().getString("userId");

        switch (type){
            case GET_FOLLOWERS_DETAILS:
                getFollowersDetails(user_id);
                break;

            case GET_FOLLOWING_DETAILS:
                getFollowingDetails(user_id);
                break;
        }

    }

    private void getFollowersDetails(final String user_id){
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        userTable = FirebaseDatabase.getInstance().getReference().child("Users");

        followersTable.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()) {
                    followersTable.child(user_id).child("accepted").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                final String the_users_id = ds.getKey();
                                userTable.child(the_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String firstName = dataSnapshot.child("first_name").getValue().toString();
                                        String lastName = dataSnapshot.child("last_name").getValue().toString();
                                        String usename = dataSnapshot.child("user_name").getValue().toString();
                                        String image = dataSnapshot.child("image").getValue().toString();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // followers_list.add()



    }

    private void getFollowingDetails(String user_id){
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        userTable = FirebaseDatabase.getInstance().getReference().child("Users");


    }

}
