package com.example.ooduberu.chatapp.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.activities.FoundFriendActivity;
import com.example.ooduberu.chatapp.activities.ProfileActivity;
import com.example.ooduberu.chatapp.model.FollowBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;

    DatabaseReference rootDatabaseHolder;
    DatabaseReference userTable;
    DatabaseReference followersTable;
    DatabaseReference followersTableTemp;
    DatabaseReference followingTable;

    Query query;

    FirebaseRecyclerOptions<FollowBody> options;
    FirebaseRecyclerAdapter<FollowBody,FollowersViewHolder> adapter;

    @BindView(R.id.find_user_searchview) SearchView find_user_searchview;
    @BindView(R.id.display_users_recycler_view) RecyclerView display_users_recycler_view;

    ArrayList<String> followingKeys = new ArrayList<>();
    ArrayList<String> acceptRequestKeys = new ArrayList<>();
    ArrayList<String> requestSentKeys = new ArrayList<>();
    ArrayList<String> followbackKeys = new ArrayList<>();

    String foundUserId;


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

        Bundle bundle = getArguments();
        if(bundle != null){
            foundUserId = bundle.getString("foundUserId");
        }

        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers").child(foundUserId).child("accepted");
        followersTableTemp = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");

        followersTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingKeys.clear();
                acceptRequestKeys.clear();
                requestSentKeys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    final String theFollowerId = ds.getKey();
                    followersTableTemp.child(theFollowerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("accepted").exists()){
                                if(dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).exists()){
                                    if (dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("accepted")){
                                        followingKeys.add(theFollowerId);

                                    }
//                                    else if (dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("accept request")){
//                                        acceptRequestKeys.add(theFollowerId);
//                                    }
                                }

                            }

                            if(dataSnapshot.child("pending").exists()){
                                if(dataSnapshot.child("pending").child(AppPreference.getCurrentUserId()).exists()){
                                    if (dataSnapshot.child("pending").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("pending")) {
                                        requestSentKeys.add(theFollowerId);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    followingTable.child(theFollowerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("pending").child(AppPreference.getCurrentUserId()).exists()){
                                if (dataSnapshot.child("pending").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("follow back")){
                                   followbackKeys.add(theFollowerId);
                                }
                                else if (dataSnapshot.child("pending").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("pending")){
                                    acceptRequestKeys.add(theFollowerId);
                                }
                            }

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



        display_users_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        display_users_recycler_view.setHasFixedSize(true);
        findUser("");

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
            adapter.startListening();
        }
    }


    private void findUser(String s){
        if(TextUtils.isEmpty(s)){
            //fetch all followers
            query = followersTable.orderByChild("user_name");
        }else{
            //fetch for currently searched follower
            query = followersTable.orderByChild("user_name").startAt(s).endAt(s + "\uf8ff");
        }

        options =
                new FirebaseRecyclerOptions.Builder<FollowBody>()
                        .setQuery(query, FollowBody.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FollowBody, FollowersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FollowersViewHolder holder, final int position, @NonNull FollowBody model) {
                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUserProfile(getRef(position).getKey(),holder.user_image);
                    }
                });

                if(getRef(position).getKey().equals(AppPreference.getCurrentUserId())){
                    holder.follow_button.setVisibility(View.GONE);
                }


                if(followingKeys.contains(getRef(position).getKey())){
                    holder.setButton("following");
                }
                else if(acceptRequestKeys.contains(getRef(position).getKey())){
                    holder.setButton("accept request");

                }
                else if(followbackKeys.contains(getRef(position).getKey())){
                    holder.setButton("follow back");
                }
                else if(requestSentKeys.contains(getRef(position).getKey())){
                    holder.setButton("request sent");
                }
                else{
                    holder.setButton("follow");
                }

                holder.follow_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toasty.error(getContext(),getRef(position).getKey()).show();
                    }
                });


                userTable.child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.setFullName(dataSnapshot.child("first_name").getValue().toString()+" "+dataSnapshot.child("last_name").getValue().toString());
                        holder.setUserName(dataSnapshot.child("user_name").getValue().toString());
                        holder.setImage(dataSnapshot.child("image").getValue().toString(),getContext());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_follower_layout,viewGroup, false);

                return new FollowersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                //todo stuffs
            }
        };

        adapter.startListening();
        display_users_recycler_view.setAdapter(adapter);

    }

    public static class FollowersViewHolder extends RecyclerView.ViewHolder{
        View userView;//creates a view that will be used by the firebase adapter
        TextView userName;
        TextView  fullName;
        CircleImageView user_image;
        MaterialButton follow_button;

        public FollowersViewHolder(@NonNull View itemView) {
            super(itemView);

            userView = itemView;
            user_image = (CircleImageView)userView.findViewById(R.id.user_image);
            follow_button = (MaterialButton)userView.findViewById(R.id.follow_button);
        }


        //creates a method to set the username of the user
        public void setUserName(String name){
            userName = (TextView)userView.findViewById(R.id.user_name);
            userName.setText(name);
        }

        //creates a method to set full name of the user
        public void setFullName(String full_name){
            fullName = (TextView)userView.findViewById(R.id.full_name);
            fullName.setText(full_name);
        }

        //sets the image of the user
        public void setImage(String image, Context context){//takes in the findFriendsActivity context  with string name
            if (image.equals("default")){
                user_image.setImageResource(R.drawable.person_placeholder);
            }
            else{
                Glide.with(context).load(image)
                        .apply(new RequestOptions().error(R.drawable.person_placeholder).placeholder(R.drawable.person_placeholder).fitCenter())
                        .into(user_image);

            }

        }

        public void setButton(String text){
            follow_button.setText(text);
        }
    }

    private void viewUserProfile(String userId, ImageView sharedImageView){
        DeviceUtils.hideKeyboard(getActivity());
        if(userId.equals(AppPreference.getCurrentUserId())){
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), sharedImageView,  "profile");
            startActivity(intent,options.toBundle());
        }else{
            Intent intent = new Intent(getActivity(), FoundFriendActivity.class).putExtra("otherUsersId",userId);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), sharedImageView,  "profile");
            startActivity(intent,options.toBundle());
        }


    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
