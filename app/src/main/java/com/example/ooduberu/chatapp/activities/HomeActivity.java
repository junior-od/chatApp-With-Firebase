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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.adapters.HomePagerAdapter;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.auth.FirebaseAuth;

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

    //for the tabs
    LinearLayout tab1;
    LinearLayout tab2;
    LinearLayout tab3;
    ImageView home_icon;
    ImageView messages_icon;
    ImageView friend_request_icon;
    TextView home_count_figure;
    TextView messages_count_figure;
    TextView friend_request_count_figure;


    @BindView(R.id.app_navigate) Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setTitle("chat-app");

        mAuth = FirebaseAuth.getInstance();

        Toasty.success(getBaseContext(), AppPreference.getCurrentUserId()).show();
        Toasty.error(getBaseContext(),AppPreference.getCurrentUserName()).show();

        tabbed_layout = (TabLayout)findViewById(R.id.tabbed_layout);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),getBaseContext(),3, AppPreference.getCurrentUserId());
        viewPager.setAdapter(homePagerAdapter);
        tabbed_layout.setupWithViewPager(viewPager);
        setUpTabs(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    tab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette));
                    tab2.setBackgroundColor(getResources().getColor(R.color.white));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
                    tab3.setBackgroundColor(getResources().getColor(R.color.white));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
                }else if(i == 1){
                    tab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
                    tab1.setBackgroundColor(getResources().getColor(R.color.white));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
                    tab3.setBackgroundColor(getResources().getColor(R.color.white));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
                }else{
                    tab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend));
                    tab1.setBackgroundColor(getResources().getColor(R.color.white));
                    home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
                    tab2.setBackgroundColor(getResources().getColor(R.color.white));
                    messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    private void setUpTabs(int type){//todo int type
        tab1 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        home_icon = (ImageView)tab1.findViewById(R.id.image_view);
        home_count_figure = (TextView)tab1.findViewById(R.id.figure);
        tabbed_layout.getTabAt(0).setCustomView(tab1);

        tab2 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        messages_icon = (ImageView)tab2.findViewById(R.id.image_view);
        messages_count_figure = (TextView)tab2.findViewById(R.id.figure);
        tabbed_layout.getTabAt(1).setCustomView(tab2);

        tab3 = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.home_custom_tab_layout, null);
        friend_request_icon = (ImageView)tab3.findViewById(R.id.image_view);
        friend_request_count_figure = (TextView)tab3.findViewById(R.id.figure);
        tabbed_layout.getTabAt(2).setCustomView(tab3);

        if(type == 0){
            tab1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette));
            tab2.setBackgroundColor(getResources().getColor(R.color.white));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
            tab3.setBackgroundColor(getResources().getColor(R.color.white));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
        }else if(type == 1){
            tab2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message));
            tab1.setBackgroundColor(getResources().getColor(R.color.white));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
            tab3.setBackgroundColor(getResources().getColor(R.color.white));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend_black));
        }else{
            tab3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            friend_request_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_friend));
            tab1.setBackgroundColor(getResources().getColor(R.color.white));
            home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_icon_silhouette_black));
            tab2.setBackgroundColor(getResources().getColor(R.color.white));
            messages_icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_black));
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
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
