package com.example.ooduberu.chatapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.interfaces.Listener;
import com.example.ooduberu.chatapp.model.FollowBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowerRequestsFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;
    Listener listener;
    Activity myActivity;

    DatabaseReference userTable;
    DatabaseReference followersTable;

    Query query;
    FirebaseRecyclerOptions<FollowBody> options;
    FirebaseRecyclerAdapter<FollowBody,FollowersFragment.FollowersViewHolder> adapter;

    @BindView(R.id.followers_request_recycler_view) RecyclerView followers_request_recycler_view;
    @BindView(R.id.no_requests_layout) FrameLayout no_requests_layout;

    public FollowerRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = getActivity();
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        myActivity = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_follower_requests, container, false);
        unbinder = ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers").child(AppPreference.getCurrentUserId()).child("pending");

        followers_request_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        followers_request_recycler_view.setHasFixedSize(true);

        displayAllPendingRequests();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();

        }
    }

    @Override
    public void onStop() {
        if(adapter != null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.notifyDataSetChanged();
            adapter.startListening();
        }
    }

    private void displayAllPendingRequests(){
        query = userTable.orderByChild("user_name");
        //indexed query takes in query of all the keys to be found in the database ref to make searching realtime

        options =
                new FirebaseRecyclerOptions.Builder<FollowBody>()
                        .setIndexedQuery(query,followersTable,FollowBody.class)
                        .build();



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
