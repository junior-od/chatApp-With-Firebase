package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.adapters.HomePagerAdapter;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseAuth mAuth;
    HomePagerAdapter homePagerAdapter;
    TabLayout tabbed_layout;
    ViewPager viewPager;
    DatabaseReference rootDatabaseHolder;
    DatabaseReference followersTable;

    //for the tabs
    LinearLayout tab1;
    LinearLayout tab2;
    LinearLayout tab3;
    LinearLayout tab4;

    RelativeLayout relativeLayout1;
    RelativeLayout relativeLayout2;
    RelativeLayout relativeLayout3;
    RelativeLayout relativeLayout4;

    ImageView home_icon;
    ImageView messages_icon;
    ImageView friend_request_icon;
    ImageView activity_icon;

    TextView home_count_figure;
    TextView messages_count_figure;
    TextView friend_request_count_figure;

    ImageView home_dot;
    ImageView messages_dot;
    ImageView friend_request_dot;
    ImageView activity_dot;

    LinearLayout.LayoutParams layoutParams;
    LinearLayout.LayoutParams defaultlayoutParams;
    LinearLayout.LayoutParams ls;

    @BindView(R.id.app_navigate) Toolbar mToolbar;

    int pos = 0;
    String verifyUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setTitle("chat-app");

        mAuth = FirebaseAuth.getInstance();

        if(getIntent().getExtras() != null){
            pos = getIntent().getExtras().getInt("position");
            verifyUserId = getIntent().getExtras().getString("userId");
            if(!verifyUserId.equals(AppPreference.getCurrentUserId())){
                signOut();
            }

        }

        Toasty.success(getBaseContext(), AppPreference.getCurrentUserId()).show();
        Toasty.error(getBaseContext(),AppPreference.getCurrentUserName()).show();



        rootDatabaseHolder = FirebaseDatabase.getInstance().getReference();
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");

        rootDatabaseHolder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getPendingRequestCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                 getPendingRequestCount();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        layoutParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.icon_width), (int)getResources().getDimension(R.dimen.icon_height));
        defaultlayoutParams = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.defaulticon_width), (int)getResources().getDimension(R.dimen.defaulticon_height));

        ls = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        tabbed_layout = (TabLayout)findViewById(R.id.tabbed_layout);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),getBaseContext(),4, AppPreference.getCurrentUserId());
        viewPager.setAdapter(homePagerAdapter);
        tabbed_layout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(pos);
        setUpTabs(pos);
        if(pos == 2){
            friend_request_dot.setVisibility(View.VISIBLE);
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                if(position == 0){
                    tab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tab1.setLayoutParams(ls);
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette));
                    home_icon.setLayoutParams(layoutParams);
                    home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
                    relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
                    home_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

                    tab2.setBackgroundColor(getResources().getColor(R.color.white));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
                    messages_icon.setLayoutParams(defaultlayoutParams);
                    messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    messages_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab3.setBackgroundColor(getResources().getColor(R.color.white));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
                    friend_request_icon.setLayoutParams(defaultlayoutParams);
                    friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab4.setBackgroundColor(getResources().getColor(R.color.white));
                    activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                    activity_icon.setLayoutParams(defaultlayoutParams);
                    activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

                }else if(position == 1){
                    tab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tab2.setLayoutParams(ls);
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
                    messages_icon.setLayoutParams(layoutParams);
                    messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
                    relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
                    messages_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

                    tab1.setBackgroundColor(getResources().getColor(R.color.white));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
                    home_icon.setLayoutParams(defaultlayoutParams);
                    home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    home_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab3.setBackgroundColor(getResources().getColor(R.color.white));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
                    friend_request_icon.setLayoutParams(defaultlayoutParams);
                    friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab4.setBackgroundColor(getResources().getColor(R.color.white));
                    activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                    activity_icon.setLayoutParams(defaultlayoutParams);
                    activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

                }else if (position == 2){
                    tab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tab3.setLayoutParams(ls);
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend));
                    friend_request_icon.setLayoutParams(layoutParams);
                    friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
                    relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
                    friend_request_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

                    tab1.setBackgroundColor(getResources().getColor(R.color.white));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
                    home_icon.setLayoutParams(defaultlayoutParams);
                    home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    home_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab2.setBackgroundColor(getResources().getColor(R.color.white));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
                    messages_icon.setLayoutParams(defaultlayoutParams);
                    messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    messages_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab4.setBackgroundColor(getResources().getColor(R.color.white));
                    activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
                    activity_icon.setLayoutParams(defaultlayoutParams);
                    activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

                }else{
                    tab4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tab4.setLayoutParams(ls);
                    activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                    activity_icon.setLayoutParams(layoutParams);
                    activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
                    relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
                    // friend_request_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

                    tab1.setBackgroundColor(getResources().getColor(R.color.white));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
                    home_icon.setLayoutParams(defaultlayoutParams);
                    home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    home_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab2.setBackgroundColor(getResources().getColor(R.color.white));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
                    messages_icon.setLayoutParams(defaultlayoutParams);
                    messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    messages_count_figure.setTextColor(getResources().getColor(R.color.white));

                    tab3.setBackgroundColor(getResources().getColor(R.color.white));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
                    friend_request_icon.setLayoutParams(defaultlayoutParams);
                    friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
                    relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
                    friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().getExtras() != null){
            pos = getIntent().getExtras().getInt("position");
            verifyUserId = getIntent().getExtras().getString("userId");
//            Toasty.error(getBaseContext(),verifyUserId).show();
            if(!verifyUserId.equals(AppPreference.getCurrentUserId())){
                signOut();
            }else{
                if(pos == 2){
                    friend_request_dot.setVisibility(View.VISIBLE);
                }
                viewPager.setCurrentItem(pos);
            }

        }else{
            viewPager.setCurrentItem(pos);

        }
    }

    private void setUpTabs(int position){//todo int type
        tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        home_icon = (ImageView)tab1.findViewById(R.id.image_view);
        home_dot = (ImageView)tab1.findViewById(R.id.dot);
        relativeLayout1 = (RelativeLayout)tab1.findViewById(R.id.relativelayout);
        home_count_figure = (TextView)tab1.findViewById(R.id.figure);
        tabbed_layout.getTabAt(0).setCustomView(tab1);

        tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        messages_icon = (ImageView)tab2.findViewById(R.id.image_view);
        messages_dot = (ImageView)tab2.findViewById(R.id.dot);
        relativeLayout2 = (RelativeLayout)tab2.findViewById(R.id.relativelayout);
        messages_count_figure = (TextView)tab2.findViewById(R.id.figure);
        tabbed_layout.getTabAt(1).setCustomView(tab2);

        tab3 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        friend_request_icon = (ImageView)tab3.findViewById(R.id.image_view);
        friend_request_dot = (ImageView)tab3.findViewById(R.id.dot);
        relativeLayout3 = (RelativeLayout)tab3.findViewById(R.id.relativelayout);
        friend_request_count_figure = (TextView)tab3.findViewById(R.id.figure);
        tabbed_layout.getTabAt(2).setCustomView(tab3);

        tab4 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        activity_icon = (ImageView)tab4.findViewById(R.id.image_view);
        activity_dot = (ImageView)tab4.findViewById(R.id.dot);
        relativeLayout4 = (RelativeLayout)tab4.findViewById(R.id.relativelayout);
        //friend_request_count_figure = (TextView)tab4.findViewById(R.id.figure);
        tabbed_layout.getTabAt(3).setCustomView(tab4);

        if(position == 0){
            tab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tab1.setLayoutParams(ls);
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette));
            home_icon.setLayoutParams(layoutParams);
            home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
            relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
            home_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

            tab2.setBackgroundColor(getResources().getColor(R.color.white));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
            messages_icon.setLayoutParams(defaultlayoutParams);
            messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            messages_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab3.setBackgroundColor(getResources().getColor(R.color.white));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
            friend_request_icon.setLayoutParams(defaultlayoutParams);
            friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab4.setBackgroundColor(getResources().getColor(R.color.white));
            activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
            activity_icon.setLayoutParams(defaultlayoutParams);
            activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

        }else if(position == 1){
            tab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tab2.setLayoutParams(ls);
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
            messages_icon.setLayoutParams(layoutParams);
            messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
            relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
            messages_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

            tab1.setBackgroundColor(getResources().getColor(R.color.white));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
            home_icon.setLayoutParams(defaultlayoutParams);
            home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            home_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab3.setBackgroundColor(getResources().getColor(R.color.white));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
            friend_request_icon.setLayoutParams(defaultlayoutParams);
            friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab4.setBackgroundColor(getResources().getColor(R.color.white));
            activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
            activity_icon.setLayoutParams(defaultlayoutParams);
            activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

        }else if (position == 2){
            tab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tab3.setLayoutParams(ls);
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend));
            friend_request_icon.setLayoutParams(layoutParams);
            friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
            relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
            friend_request_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

            tab1.setBackgroundColor(getResources().getColor(R.color.white));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
            home_icon.setLayoutParams(defaultlayoutParams);
            home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            home_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab2.setBackgroundColor(getResources().getColor(R.color.white));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
            messages_icon.setLayoutParams(defaultlayoutParams);
            messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            messages_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab4.setBackgroundColor(getResources().getColor(R.color.white));
            activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black));
            activity_icon.setLayoutParams(defaultlayoutParams);
            activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            //friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));

        }else{
            tab4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tab4.setLayoutParams(ls);
            activity_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
            activity_icon.setLayoutParams(layoutParams);
            activity_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot));
            relativeLayout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background));
           // friend_request_count_figure.setTextColor(getResources().getColor(R.color.colorPrimary));

            tab1.setBackgroundColor(getResources().getColor(R.color.white));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
            home_icon.setLayoutParams(defaultlayoutParams);
            home_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            home_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab2.setBackgroundColor(getResources().getColor(R.color.white));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
            messages_icon.setLayoutParams(defaultlayoutParams);
            messages_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout2.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            messages_count_figure.setTextColor(getResources().getColor(R.color.white));

            tab3.setBackgroundColor(getResources().getColor(R.color.white));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
            friend_request_icon.setLayoutParams(defaultlayoutParams);
            friend_request_dot.setImageDrawable(getResources().getDrawable(R.drawable.ic_dot_black));
            relativeLayout3.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_background_black));
            friend_request_count_figure.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.findFriendsLink:
                startActivity(new Intent(getBaseContext(),FindFriendActivity.class));
                break;

            case R.id.settings_link:
                startActivity(new Intent(getBaseContext(),SettingsActivity.class));
                break;

            case R.id.log_out_link:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.sign_out)
    public void signOut(){
        mAuth.signOut();
        AppPreference.setCheckPendingRequestCount(false);
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }

    private void getPendingRequestCount(){
        followersTable.child(AppPreference.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("pending").exists()){
                    relativeLayout3.setVisibility(View.VISIBLE);
                    friend_request_count_figure.setText(dataSnapshot.child("pending").getChildrenCount()+"");
                    if(AppPreference.IsPendingRequestCounted()){
                        if(dataSnapshot.child("pending").getChildrenCount() > AppPreference.getPendingRequestsCount()){
                            friend_request_dot.setVisibility(View.VISIBLE);
                            AppPreference.setPendingRequestsCount(dataSnapshot.child("pending").getChildrenCount());
                        }

                    }


                }else{
                    AppPreference.setPendingRequestsCount(0);
                    friend_request_dot.setVisibility(View.GONE);
                    relativeLayout3.setVisibility(View.GONE);
                    friend_request_count_figure.setText(dataSnapshot.child("pending").getChildrenCount()+"");
                }
                AppPreference.setCheckPendingRequestCount(true);

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
