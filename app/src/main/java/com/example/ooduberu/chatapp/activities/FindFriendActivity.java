package com.example.ooduberu.chatapp.activities;


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ooduberu.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FindFriendActivity extends BaseActivity {
    Unbinder unbinder;

    @BindView(R.id.toolbar) Toolbar search_nav_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(search_nav_toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();//initiate the menu bar
        menuInflater.inflate(R.menu.search_menu,menu);//launch the menu layout
        MenuItem item = menu.findItem(R.id.search_button);//item to identify the search button
        android.widget.SearchView searchView =(android.widget.SearchView) MenuItemCompat.getActionView(item);//initiate with search view
        searchView.onActionViewExpanded();
        searchView.setQueryHint("search...");
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                //findTheUser(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {



                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
