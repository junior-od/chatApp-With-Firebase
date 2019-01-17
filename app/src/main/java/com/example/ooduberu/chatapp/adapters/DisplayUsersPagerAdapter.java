package com.example.ooduberu.chatapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.ooduberu.chatapp.fragments.FollowersFragment;
import com.example.ooduberu.chatapp.fragments.FollowingFragment;

public class DisplayUsersPagerAdapter extends FragmentPagerAdapter {
    int tabCount;
    Context context;

    public DisplayUsersPagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.context = context;
        this.tabCount = numberOfTabs;
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
                return followersFragment;

            case 1:
                FollowingFragment followingFragment = new FollowingFragment();
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
