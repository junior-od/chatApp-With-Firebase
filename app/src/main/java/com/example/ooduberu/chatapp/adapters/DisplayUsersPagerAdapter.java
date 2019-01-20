package com.example.ooduberu.chatapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.ooduberu.chatapp.fragments.FollowersFragment;
import com.example.ooduberu.chatapp.fragments.FollowingFragment;

public class DisplayUsersPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    Context context;
    String users_id;

    public DisplayUsersPagerAdapter(FragmentManager fm, int numberOfTabs,String users_id) {
        super(fm);
        this.context = context;
        this.tabCount = numberOfTabs;
        this.users_id = users_id;
    }

//    public View getTabView(int position,int figure,String title){
//        View v = LayoutInflater.from(context).inflate(R.layout.display_user_custom_tab_layout, null);
//        TextView tab_figure = (TextView)v.findViewById(R.id.figure);
//        TextView tab_title = (TextView)v.findViewById(R.id.figure_title);
//        tab_figure.setText(figure+"");
//        tab_title.setText(title);
//
//        return v;
//    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FollowersFragment followersFragment = new FollowersFragment();
                Bundle bundle = new Bundle();
                bundle.putString("foundUserId",users_id);
                followersFragment.setArguments(bundle);

                return followersFragment;

            case 1:
                FollowingFragment followingFragment = new FollowingFragment();
                Bundle bundles = new Bundle();
                bundles.putString("foundUserId",users_id);
                followingFragment.setArguments(bundles);

                return followingFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
