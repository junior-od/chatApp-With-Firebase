package com.example.ooduberu.chatapp.activities;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoundFriendActivity extends AppCompatActivity {
    Unbinder unbinder;
    DatabaseReference userTable;

    @BindView(R.id.app_navigate) Toolbar mToolbar;
    @BindView(R.id.user_header_image) ImageView user_header_image;
    @BindView(R.id.user_profile_image) ImageView user_profile_image;
    @BindView(R.id.posts_figure) TextView posts_figure;
    @BindView(R.id.followers_figure) TextView followers_figure;
    @BindView(R.id.following_figure) TextView following_figure;
    @BindView(R.id.follow_button) MaterialButton follow_button;

    String uId;
    String otherUsersId;
    String headerImage;
    String profile_image;
    String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_friend);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uId = AppPreference.getCurrentUserId();
        otherUsersId = getIntent().getExtras().getString("otherUsersId");

        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        userTable.keepSynced(true);

        userTable.child(otherUsersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullName = dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString();
                headerImage = dataSnapshot.child("header_image").getValue().toString();
                profile_image = dataSnapshot.child("image").getValue().toString();
                //to display the  header image from firebase
                if(headerImage.equals("default")){
                    user_header_image.setImageResource(R.drawable.default_header_background);
                }else{
                    Glide.with(getBaseContext()).load(headerImage)
                            .apply(new RequestOptions().error(R.drawable.default_header_background).placeholder(R.drawable.default_header_background).fitCenter())
                            .into(user_header_image);
                }

                //to display the profile image from firebase
                if(profile_image.equals("default")){
                    user_profile_image.setImageResource(R.drawable.person_placeholder);
                }else{
                    Glide.with(getBaseContext()).load(profile_image)
                            .apply(new RequestOptions().error(R.drawable.person_placeholder).placeholder(R.drawable.person_placeholder).fitCenter())
                            .into(user_profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
