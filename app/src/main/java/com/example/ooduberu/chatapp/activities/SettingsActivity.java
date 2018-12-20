package com.example.ooduberu.chatapp.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    Unbinder unbinder;
    DatabaseReference userTable;

    @BindView(R.id.user_profile_image) CircleImageView user_profile_image;
    @BindView(R.id.user_name) TextView user_name;
    @BindView(R.id.user_status) TextView user_status;
    @BindView(R.id.app_navigate) Toolbar mToolbar;

    String uId;
    String profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        uId = AppPreference.getCurrentUserId();
        userTable = FirebaseDatabase.getInstance().getReference().child("Users");

        userTable.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_name.setText(dataSnapshot.child("user_name").getValue().toString());
                user_status.setText(dataSnapshot.child("status").getValue().toString());

                //to display the image from firebase
                profileImage = dataSnapshot.child("image").getValue().toString();
                if (profileImage.equals("default")) {
                    user_profile_image.setImageResource(R.drawable.person_placeholder);
                }
                else{
                    Glide.with(getBaseContext()).load(profileImage)
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
