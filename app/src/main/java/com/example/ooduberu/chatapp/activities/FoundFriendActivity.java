package com.example.ooduberu.chatapp.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.model.FollowBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class FoundFriendActivity extends BaseActivity {
    Unbinder unbinder;
    DatabaseReference userTable;
    DatabaseReference followersTable;
    DatabaseReference followingTable;

    @BindView(R.id.app_navigate) Toolbar mToolbar;
    @BindView(R.id.user_header_image) ImageView user_header_image;
    @BindView(R.id.user_profile_image) ImageView user_profile_image;
    @BindView(R.id.full_name) TextView full_name;
    @BindView(R.id.user_name) TextView user_name;
    @BindView(R.id.user_status) TextView user_status;
    @BindView(R.id.posts_figure) TextView posts_figure;
    @BindView(R.id.followers_figure) TextView followers_figure;
    @BindView(R.id.following_figure) TextView following_figure;
    @BindView(R.id.follow_button) Button follow_button;

    String uId;
    String otherUsersId;
    String headerImage;
    String profile_image;
    String fullName;
    String userName;
    String userStatus;
    String accountType;

    int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };

    int[] colors = new int[] {
            Color.WHITE,
            Color.RED,
            Color.GREEN,
            Color.BLUE
    };

    ColorStateList myList = new ColorStateList(states, colors);

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
                userName = dataSnapshot.child("user_name").getValue().toString();
                userStatus = dataSnapshot.child("status").getValue().toString();
                accountType = dataSnapshot.child("account_type").getValue().toString();

                full_name.setText(fullName);
                user_name.setText("@"+userName);
                getSupportActionBar().setTitle(userName);
                user_status.setText(userStatus);
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

        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followersTable.keepSynced(true);

        followersTable.child(otherUsersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    followers_figure.setText(dataSnapshot.child("accepted").getChildrenCount()+"");
                    if(dataSnapshot.child("accepted").child(uId).exists()){
                        if (dataSnapshot.child("accepted").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("accepted")){
                            follow_button.setText("following");
                            follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
                            follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                            follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                follow_button.setCompoundDrawableTintList(myList);
                            }
                        }
                    }
                }

                if(dataSnapshot.child("pending").exists()){
                    if(dataSnapshot.child("pending").child(uId).exists()){
                        if (dataSnapshot.child("pending").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("pending")){
                            follow_button.setText("request sent");
                            follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
                            follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                            follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                follow_button.setCompoundDrawableTintList(myList);
                            }
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        followingTable.keepSynced(true);

        followingTable.child(otherUsersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    following_figure.setText(dataSnapshot.getChildrenCount()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.follow_button)
    public void followOrUnfollow(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }

        if (follow_button.getText().toString().trim().equalsIgnoreCase("follow")){
            follow();

        }else if (follow_button.getText().toString().trim().equalsIgnoreCase("request sent")){
            cancelRequest();
        }else{
            unfollow();
        }

    }

    private void follow(){
        showProgressLoader();
        if(accountType.equalsIgnoreCase("unlocked")){
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("accepted");
            followersTable.child(otherUsersId).child("accepted").child(uId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressLoader();
                    if(task.isSuccessful()){
                        followingTable.child(uId).child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toasty.success(getBaseContext(),"now following").show();
                                }else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else{
                        Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }else{
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("pending");
            followersTable.child(otherUsersId).child("pending").child(uId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressLoader();
                    if (task.isSuccessful()){
                        Toasty.success(getBaseContext(),"request sent").show();
                    }else{
                        Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }

    }

    private void cancelRequest(){
        showProgressLoader();
        followersTable.child(otherUsersId).child("pending").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressLoader();
                if (task.isSuccessful()){
                    follow_button.setText("follow");
                    follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add,0,0,0);
                    Toasty.success(getBaseContext(),"request cancelled").show();
                }else{
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    private void unfollow(){
        showProgressLoader();
        followersTable.child(otherUsersId).child("accepted").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressLoader();
                if(task.isSuccessful()){
                    follow_button.setText("follow");
                    follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add,0,0,0);
                    Toasty.success(getBaseContext(),"unfollowed").show();
                }else{
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
