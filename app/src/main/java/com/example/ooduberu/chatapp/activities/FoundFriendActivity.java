package com.example.ooduberu.chatapp.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.ChildEventListener;
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
    DatabaseReference rootDatabaseHolder;
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
    TextView followers_figure;
    TextView following_figure;
    Button follow_button;

    String follow_button_text = "";

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
            Color.parseColor("#54d9c8"),
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

        followers_figure = (TextView)findViewById(R.id.followers_figure);
        following_figure = (TextView)findViewById(R.id.following_figure);
        follow_button = (Button)findViewById(R.id.follow_button);

        uId = AppPreference.getCurrentUserId();
        otherUsersId = getIntent().getExtras().getString("otherUsersId");


        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followOrUnfollow();
            }
        });

        rootDatabaseHolder = FirebaseDatabase.getInstance().getReference();

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
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        followingTable.keepSynced(true);

        //new  code testing
        rootDatabaseHolder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchFollowers();
                fetchFollowing();
                setDefaultButtonText();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toasty.success(getBaseContext(),"changed").show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Toasty.success(getBaseContext(),"removed").show();
                if(dataSnapshot.hasChild("followers")){

                }else{
                    followers_figure.setText("0");
                }

                if(dataSnapshot.hasChild("following")){

                }else{
                    following_figure.setText("0");
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 Toasty.success(getBaseContext(),"moved").show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.success(getBaseContext(),"cancelled").show();
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

    private void setDefaultButtonText(){
        if(follow_button_text.isEmpty()){
            follow_button.setText("follow");
            follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
            follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }


    private void fetchFollowers(){
        followersTable.child(otherUsersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("accepted").exists()){
                    followersTable.child(otherUsersId).child("accepted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            followers_figure.setText(dataSnapshot.getChildrenCount()+"");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if(dataSnapshot.child("accepted").child(uId).exists()){
                        if (dataSnapshot.child("accepted").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("accepted")){
                            follow_button_text = "following";
                            follow_button.setText(follow_button_text);
                            follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
                            follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                            follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                follow_button.setCompoundDrawableTintList(myList);
                            }
                        }else if (dataSnapshot.child("accepted").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("accept request")){
                            follow_button_text = "accept request";
                            follow_button.setText(follow_button_text);
                        }

                    }

                }else{
                    followers_figure.setText("0");
                }

                if(dataSnapshot.child("pending").exists()){
                    if(dataSnapshot.child("pending").child(uId).exists()){
                        if (dataSnapshot.child("pending").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("pending")) {
                            follow_button_text = "request sent";
                            follow_button.setText(follow_button_text);
                            follow_button.setBackground(getResources().getDrawable(R.drawable.follow_button_drawable));
                            follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                            follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
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
    }

    private void fetchFollowing(){

        followingTable.child(otherUsersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if (dataSnapshot.child("accepted").exists()){
                        following_figure.setText(dataSnapshot.child("accepted").getChildrenCount()+"");

                    }
                }
                if(dataSnapshot.child("pending").child(uId).exists()){
                    if (dataSnapshot.child("pending").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("follow back")){
                        follow_button_text = "follow back";
                        follow_button.setText(follow_button_text);
                        follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
                        follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                        follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
                        // follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            follow_button.setCompoundDrawableTintList(myList);
                        }
                    }
                    else if (dataSnapshot.child("pending").child(uId).child("request_type").getValue().toString().equalsIgnoreCase("pending")){
                        follow_button_text = "accept request";
                        follow_button.setText(follow_button_text);
                        follow_button.setBackgroundColor(Color.parseColor("#ffffff"));
                        follow_button.setTextColor(getResources().getColor(R.color.customBlue));
                        // follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check,0,0,0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            follow_button.setCompoundDrawableTintList(myList);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void followOrUnfollow(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }

        if (follow_button.getText().toString().trim().equalsIgnoreCase("follow")){
            follow();

        }else if (follow_button.getText().toString().trim().equalsIgnoreCase("request sent")){
            cancelRequest();
        }else if( follow_button.getText().toString().trim().equalsIgnoreCase("follow back")){
            followBack();
        }else if (follow_button.getText().toString().trim().equalsIgnoreCase("accept request")){
            acceptRequest();
        }
        else if (follow_button.getText().toString().trim().equalsIgnoreCase("following")){
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
                    if(task.isSuccessful()){
                        followingTable.child(uId).child("accepted").child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FollowBody fb = new FollowBody();
                                    fb.setRequest_type("follow back");
                                    followingTable.child(uId).child("pending").child(otherUsersId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hideProgressLoader();
                                            if (task.isSuccessful()){
                                                fetchFollowers();

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
                                } else{
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
                    if (task.isSuccessful()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followingTable.child(uId).child("pending").child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressLoader();
                                if(task.isSuccessful()){
                                    fetchFollowers();
                                    Toasty.success(getBaseContext(),"request sent").show();
                                }
                                else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }
                            }
                        });

                    }else{
                        hideProgressLoader();
                        Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }


    }

    private void followBack(){
        showProgressLoader();
        if(accountType.equalsIgnoreCase("unlocked")){
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("accepted");
            followersTable.child(otherUsersId).child("accepted").child(uId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        followingTable.child(uId).child("accepted").child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    followingTable.child(otherUsersId).child("pending").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hideProgressLoader();
                                            if(task.isSuccessful()){
                                                fetchFollowers();
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
                    if (task.isSuccessful()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followingTable.child(uId).child("pending").child(otherUsersId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressLoader();
                                if(task.isSuccessful()){
                                    followingTable.child(otherUsersId).child("pending").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //check if the locked account already follows your account
                                                checkIfLockedAccountFollows();


                                            }else{
                                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                            }
                                        }
                                    });

                                }
                                else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }
                            }
                        });

                    }else{
                        hideProgressLoader();
                        Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }

    }

    private void acceptRequest(){
        showProgressLoader();
        final FollowBody followBody = new FollowBody();
        followBody.setRequest_type("accepted");
        followersTable.child(uId).child("accepted").child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    followingTable.child(otherUsersId).child("accepted").child(uId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followersTable.child(uId).child("pending").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            followingTable.child(otherUsersId).child("pending").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //check if this user follows the other account
                                                        checkIfCurrentUserFollows();

                                                    }  else{
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
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }
                    });
                } else{
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                }
            }
        });

    }

    private void cancelRequest(){
        showProgressLoader();
        followersTable.child(otherUsersId).child("pending").child(uId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressLoader();
                if (task.isSuccessful()){
                    followingTable.child(uId).child("pending").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                checkIfLockedAccountFollowsYou();
                            } else{
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

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
                    followingTable.child(uId).child("accepted").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                //check if the user was following before
                                checkForPendingRequestInFollowingTable();

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
    }

    private void checkForPendingRequestInFollowingTable(){
        followersTable.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").child(otherUsersId).exists()){
                    FollowBody fb = new FollowBody();
                    fb.setRequest_type("follow back");
                    followingTable.child(otherUsersId).child("pending").child(uId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                follow_button_text = "";
                                fetchFollowers();
                                fetchFollowing();
                                setDefaultButtonText();
                                Toasty.success(getBaseContext(),"unfollowed").show();
                            }else{
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }
                else{
                    followingTable.child(uId).child("pending").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                follow_button_text = "";
                                fetchFollowers();
                                fetchFollowing();
                                setDefaultButtonText();
                                Toasty.success(getBaseContext(),"unfollowed").show();
                            }else{
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkIfLockedAccountFollowsYou(){
        followersTable.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(otherUsersId).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(uId).child("accepted").child(otherUsersId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FollowBody fb = new FollowBody();
                                    fb.setRequest_type("follow back");
                                    followingTable.child(otherUsersId).child("pending").child(uId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                fetchFollowers();
                                                fetchFollowing();

                                                Toasty.success(getBaseContext(),"request cancelled").show();
                                            }
                                            else{
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
                        follow_button_text = "";
                        fetchFollowers();
                        fetchFollowing();
                        setDefaultButtonText();

                        Toasty.success(getBaseContext(),"request cancelled").show();
                    }
                }else{
                    follow_button_text = "";
                    fetchFollowers();
                    fetchFollowing();
                    setDefaultButtonText();

                    Toasty.success(getBaseContext(),"request cancelled").show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfLockedAccountFollows(){
        followersTable.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(otherUsersId).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followersTable.child(uId).child("accepted").child(otherUsersId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    fetchFollowers();
                                    Toasty.success(getBaseContext(),"request sent").show();
                                }else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else{
                        fetchFollowers();
                        Toasty.success(getBaseContext(),"request sent").show();
                    }
                }else{
                    fetchFollowers();
                    Toasty.success(getBaseContext(),"request sent").show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfCurrentUserFollows(){
        followersTable.child(otherUsersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(uId).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(otherUsersId).child("accepted").child(uId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressLoader();
                                if(task.isSuccessful()){
                                    fetchFollowers();
                                    fetchFollowing();

                                    //follow_button.setText("following");
                                    Toasty.success(getBaseContext(),"request accepted").show();
                                }else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }

                            }
                        });

                    }else{
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("follow back");
                        followingTable.child(otherUsersId).child("pending").child(uId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressLoader();
                                if (task.isSuccessful()){
                                    fetchFollowers();
                                    fetchFollowing();

                                    //follow_button.setText("following");
                                    Toasty.success(getBaseContext(),"request accepted").show();
                                }else{
                                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                }else{
                    FollowBody fb = new FollowBody();
                    fb.setRequest_type("follow back");
                    followingTable.child(otherUsersId).child("pending").child(uId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressLoader();
                            if (task.isSuccessful()){
                                fetchFollowers();
                                fetchFollowing();

                                //follow_button.setText("following");
                                Toasty.success(getBaseContext(),"request accepted").show();
                            }else{
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }
                    });
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
