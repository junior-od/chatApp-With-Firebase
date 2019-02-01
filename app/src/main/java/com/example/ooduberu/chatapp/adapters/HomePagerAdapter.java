package com.example.ooduberu.chatapp.adapters;

import android.content.Context;
import android.os.Bundle;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ooduberu.chatapp.fragments.FollowerRequestsFragment;
import com.example.ooduberu.chatapp.fragments.FollowersFragment;
import com.example.ooduberu.chatapp.fragments.HomeFragment;
import com.example.ooduberu.chatapp.fragments.MessagesFragment;
import com.example.ooduberu.chatapp.fragments.UserActivitiesFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    Context context;
    int count;
    String user_id;


    public HomePagerAdapter(FragmentManager fm, Context context, int count, String user_id) {
        super(fm);
        this.context= context;
        this.count = count;
        this.user_id = user_id;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                HomeFragment homeFragment = new HomeFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("foundUserId",users_id);
//                followersFragment.setArguments(bundle);
                return homeFragment;
            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("foundUserId",users_id);
//                followersFragment.setArguments(bundle);
                return messagesFragment;
            case 2:
                FollowerRequestsFragment followerRequestsFragment = new FollowerRequestsFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("foundUserId",user_id);
//                follo.setArguments(bundle);
                return followerRequestsFragment;

            case 3:
                UserActivitiesFragment userActivitiesFragment = new UserActivitiesFragment();

                return userActivitiesFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return count;
    }
}
