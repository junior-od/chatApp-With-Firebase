package com.example.ooduberu.chatapp.activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.adapters.DisplayUsersPagerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class DisplayUsersActivity extends AppCompatActivity {
    Unbinder unbinder;
    DisplayUsersPagerAdapter displayUsersPagerAdapter;
    TabLayout tabbed_layout;
    ViewPager viewPager;
    DatabaseReference rootDatabaseHolder;
    DatabaseReference followersTable;
    DatabaseReference followingTable;

    //for the tabs
    TextView followers_figure;
    TextView following_figure;
    TextView figure_title1;
    TextView figure_title2;

    @BindView(R.id.title) TextView title;

    ArrayList<String> navTitles = new ArrayList<>();

    String userName;
    String foundUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        unbinder = ButterKnife.bind(this);

        foundUserId = getIntent().getStringExtra("foundUserId");


        //firebase database manipulation
        rootDatabaseHolder = FirebaseDatabase.getInstance().getReference();

        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followersTable.keepSynced(true);

        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        followingTable.keepSynced(true);

        rootDatabaseHolder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getFollowersCount();
                getFollowingCount();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navTitles.clear();
        navTitles.add("FOLLOWERS");
        navTitles.add("FOLLOWING");

        tabbed_layout = (TabLayout)findViewById(R.id.tabbed_layout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);

//        tabbed_layout.addTab(tabbed_layout.newTab().setText("56576\nfollowers"));
//        tabbed_layout.addTab(tabbed_layout.newTab().setText("following"));
        //tabbed_layout.setupWithViewPager(viewPager);
        userName = getIntent().getStringExtra("userName");
        title.setText("@"+userName);

        displayUsersPagerAdapter = new DisplayUsersPagerAdapter(getSupportFragmentManager(),2);
        viewPager.setAdapter(displayUsersPagerAdapter);
        tabbed_layout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                viewPager.setCurrentItem(i);
                if(following_figure != null){
                    if(i == 0){
                        followers_figure.setTextColor(getResources().getColor(R.color.colorPrimary));
                        figure_title1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        following_figure.setTextColor(getResources().getColor(R.color.darkGrey));
                        figure_title2.setTextColor(getResources().getColor(R.color.darkGrey));
                    }else{
                        following_figure.setTextColor(getResources().getColor(R.color.colorPrimary));
                        figure_title2.setTextColor(getResources().getColor(R.color.colorPrimary));
                        followers_figure.setTextColor(getResources().getColor(R.color.darkGrey));
                        figure_title1.setTextColor(getResources().getColor(R.color.darkGrey));
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        tabbed_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition(),true);
//                setupTabbedLayout(tab.getPosition());
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        if(getIntent().getStringExtra("type").equalsIgnoreCase("followers")){
            viewPager.setCurrentItem(0);
            setUpTabs(0);

        }else {
            viewPager.setCurrentItem(1);
            setUpTabs(1);
        }


    }

    @OnClick(R.id.back_btn)
    public void goBack(){
        onBackPressed();
    }

    private void getFollowersCount(){
        followersTable.child(foundUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()) {
                    followersTable.child(foundUserId).child("accepted").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            followers_figure.setText(dataSnapshot.getChildrenCount() + "");
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

    private void getFollowingCount(){
        followingTable.child(foundUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.child("accepted").exists()){
                        following_figure.setText(dataSnapshot.child("accepted").getChildrenCount()+"");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpTabs(int type){
        LinearLayout tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.display_user_custom_tab_layout, null);
        followers_figure = (TextView)tab1.findViewById(R.id.figure);

       // followers_figure.setText(followersCount);
        figure_title1 = (TextView)tab1.findViewById(R.id.figure_title);
        figure_title1.setText("FOLLOWERS");
        tabbed_layout.getTabAt(0).setCustomView(tab1);

        LinearLayout tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.display_user_custom_tab_layout, null);
        following_figure = (TextView)tab2.findViewById(R.id.figure);
        following_figure.setText("0");
        figure_title2 = (TextView)tab2.findViewById(R.id.figure_title);
        figure_title2.setText("FOLLOWING");
        tabbed_layout.getTabAt(1).setCustomView(tab2);

        if(type == 0){
            followers_figure.setTextColor(getResources().getColor(R.color.colorPrimary));
            figure_title1.setTextColor(getResources().getColor(R.color.colorPrimary));
            following_figure.setTextColor(getResources().getColor(R.color.darkGrey));
            figure_title2.setTextColor(getResources().getColor(R.color.darkGrey));
        }else{
            following_figure.setTextColor(getResources().getColor(R.color.colorPrimary));
            figure_title2.setTextColor(getResources().getColor(R.color.colorPrimary));
            followers_figure.setTextColor(getResources().getColor(R.color.darkGrey));
            figure_title1.setTextColor(getResources().getColor(R.color.darkGrey));
        }
    }

    //unused for now
    private void setupTabbedLayout(int type){
//        // loop through all navigation tabs
        for (int i = 0; i < tabbed_layout.getTabCount(); i++) {
            LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.display_user_custom_tab_layout, null);

            // get child TextView and ImageView from this layout for the icon and label
            TextView tab_figure = (TextView) tab.findViewById(R.id.figure);
            TextView tab_title = (TextView) tab.findViewById(R.id.figure_title);

            tab_title.setText(navTitles.get(i));

            if(i == type){
                tab_figure.setTextColor(getResources().getColor(R.color.colorPrimary));
                tab_title.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            else{
                tab_figure.setTextColor(getResources().getColor(R.color.darkGrey));
                tab_title.setTextColor(getResources().getColor(R.color.darkGrey));
            }

            // finally publish this custom view to navigation tab
            tabbed_layout.getTabAt(i).setCustomView(tab);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
