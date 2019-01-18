package com.example.ooduberu.chatapp.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ooduberu.chatapp.R;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;

    DatabaseReference rootDatabaseHolder;
    DatabaseReference userTable;
    DatabaseReference followersTable;
    DatabaseReference followingTable;

    @BindView(R.id.find_user_searchview) SearchView find_user_searchview;
    @BindView(R.id.display_users_recycler_view) RecyclerView display_users_recycler_view;


    public FollowersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_followers, container, false);
        unbinder = ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        find_user_searchview.onActionViewExpanded();
        find_user_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;


            }

            @Override
            public boolean onQueryTextChange(String s) {
                findUser(s);

                return false;
            }
        });
    }


    private void findUser(String s){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
