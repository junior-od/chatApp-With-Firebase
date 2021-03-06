package com.example.ooduberu.chatapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.activities.FoundFriendActivity;
import com.example.ooduberu.chatapp.activities.ProfileActivity;
import com.example.ooduberu.chatapp.interfaces.Listener;
import com.example.ooduberu.chatapp.model.FollowBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.example.ooduberu.chatapp.utility.TimeDateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;
    Listener listener;
    Activity myActivity;
    Handler handler = new Handler();

    DatabaseReference userTable;
    DatabaseReference followersTable;
    DatabaseReference  followingTableTemp;
    DatabaseReference followingTable;
    DatabaseReference acceptFollowRequestNotifications;


    Query query;
    FirebaseRecyclerOptions<FollowBody> options;
    FirebaseRecyclerAdapter<FollowBody,FollowersFragment.FollowersViewHolder> adapter;

    @BindView(R.id.find_user_searchview) SearchView find_user_searchview;
    @BindView(R.id.display_users_recycler_view) RecyclerView display_users_recycler_view;
    @BindView(R.id.refresh_follower_list_layout) FrameLayout refresh_follower_list_layout;
    @BindView(R.id.no_result_found_layout) FrameLayout no_result_found_layout;

    ArrayList<String> followingKeys = new ArrayList<>();
    ArrayList<String> acceptRequestKeys = new ArrayList<>();
    ArrayList<String> requestSentKeys = new ArrayList<>();
    ArrayList<String> followbackKeys = new ArrayList<>();

    String foundUserId;
    String followActivityKey;
    String removeAcceptActivityKey;


    public FollowingFragment() {
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
        View v = inflater.inflate(R.layout.fragment_following, container, false);
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
        followingKeys.clear();
        acceptRequestKeys.clear();
        requestSentKeys.clear();
        followbackKeys.clear();

        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");
        followingTableTemp = FirebaseDatabase.getInstance().getReference().child("following").child(foundUserId).child("accepted");
        acceptFollowRequestNotifications = FirebaseDatabase.getInstance().getReference().child("acceptRequestNotification");

        refreshList();

        display_users_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        display_users_recycler_view.setHasFixedSize(true);
        refresh_follower_list_layout.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findUser("");
            }
        },5000);

        find_user_searchview.onActionViewExpanded();
        find_user_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findUser(s);
                    }
                },3000);
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
            refresh_follower_list_layout.setVisibility(View.VISIBLE);
            refreshList();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    if(myActivity != null){
                        refresh_follower_list_layout.setVisibility(View.GONE);
                    }

                }
            },5000);
            adapter.startListening();
        }
    }

    private void refreshList(){
        followingTableTemp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingKeys.clear();
                acceptRequestKeys.clear();
                requestSentKeys.clear();
                followbackKeys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    final String theFollowerId = ds.getKey();
                    followersTable.child(theFollowerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("accepted").exists()){
                                if(dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).exists()){
                                    if (dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("accepted")){
                                        followingKeys.add(theFollowerId);
                                    }
                                    else if (dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).child("request_type").getValue().toString().equalsIgnoreCase("accept request")){
                                        acceptRequestKeys.add(theFollowerId);
                                    }
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
    }

    private void findUser(String s){
        if(TextUtils.isEmpty(s)){
            //fetch all followers
            query =  followingTableTemp.orderByChild("time_followed");
            //todo query according to the time thr person followed the user
        }else{
            //fetch for currently searched follower
            query = userTable.orderByChild("user_name").startAt(s).endAt(s + "\uf8ff");
        }

        //indexed query takes in query of all the keys to be found in the database ref to make searching realtime

        options =
                new FirebaseRecyclerOptions.Builder<FollowBody>()
                        .setIndexedQuery(query,followingTableTemp,FollowBody.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FollowBody, FollowersFragment.FollowersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FollowersFragment.FollowersViewHolder holder, final int position, @NonNull FollowBody model) {
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

                userTable.child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(myActivity == null){
                            return;
                        }
                        holder.setFullName(dataSnapshot.child("first_name").getValue().toString()+" "+dataSnapshot.child("last_name").getValue().toString());
                        holder.setUserName(dataSnapshot.child("user_name").getValue().toString());
                        holder.setImage(dataSnapshot.child("image").getValue().toString(),myActivity);

                        holder.follow_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!NetworkUtils.isNetworkAvailable(getContext())){
                                    Toasty.warning(getContext(),"no internet connection").show();
                                    return;
                                }

                                if (holder.follow_button.getText().toString().trim().equalsIgnoreCase("follow")){
                                    follow(getRef(position).getKey(),dataSnapshot.child("account_type").getValue().toString(),position);

                                }else if (holder.follow_button.getText().toString().trim().equalsIgnoreCase("request sent")){
                                    cancelRequest(getRef(position).getKey(),position);
                                }else if( holder.follow_button.getText().toString().trim().equalsIgnoreCase("follow back")){
                                    followBack(getRef(position).getKey(),dataSnapshot.child("account_type").getValue().toString(),position);
                                }else if (holder.follow_button.getText().toString().trim().equalsIgnoreCase("accept request")){
                                    acceptRequest(getRef(position).getKey(),position);
                                }
                                else if (holder.follow_button.getText().toString().trim().equalsIgnoreCase("following")){
                                    unfollow(getRef(position).getKey(),position);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FollowersFragment.FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_follower_layout,viewGroup, false);

                return new FollowersFragment.FollowersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                //todo stuffs
                if(adapter.getItemCount() == 0){
                    display_users_recycler_view.setVisibility(View.GONE);
                    no_result_found_layout.setVisibility(View.VISIBLE);
                    refresh_follower_list_layout.setVisibility(View.GONE);
                }else{
                    display_users_recycler_view.setVisibility(View.VISIBLE);
                    no_result_found_layout.setVisibility(View.GONE);
                    refresh_follower_list_layout.setVisibility(View.GONE);
                }


            }
        };
        if(myActivity != null){
            adapter.startListening();
            display_users_recycler_view.setAdapter(adapter);
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

    private void follow(final String id, String accountType, final int position){
        listener.showProgressLoader();
        if(accountType.equalsIgnoreCase("unlocked")){
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("accepted");
            followBody.setActivity_id("");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
                long timeInMilliseconds = mDate.getTime();
                //  System.out.println("Date in milli :: " + timeInMilliseconds);
                followBody.setTime_followed(-timeInMilliseconds);
                //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            followersTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        refreshList();
                        followingTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FollowBody fb = new FollowBody();
                                    fb.setRequest_type("follow back");
                                    followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            listener.hideProgressLoader();
                                            if (task.isSuccessful()){
                                                Toasty.success(getContext(),"now following").show();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                },2000);
                                                listener.sendNotification("follow_unlocked",AppPreference.getCurrentUserId(),id,"");

                                            }else{
                                                Toasty.error(getContext(),task.getException().getMessage()).show();
                                            }
                                        }
                                    });
                                }else{
                                    listener.hideProgressLoader();
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    } else{
                        listener.hideProgressLoader();
                        Toasty.error(getContext(),task.getException().getMessage()).show();
                    }
                }
            });


        }else{
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("pending");
            followersTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        refreshList();
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if(task.isSuccessful()){
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
                                        }
                                    },2000);
                                    Toasty.success(getContext(),"request sent").show();
                                    listener.sendNotification("follow_locked",AppPreference.getCurrentUserId(),id,"");
                                }
                                else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });

                    }else{
                        listener.hideProgressLoader();
                        Toasty.error(getContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }


    }

    private void followBack(final String id, String accountType, final int position){
        listener.showProgressLoader();
        if(accountType.equalsIgnoreCase("unlocked")){
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("accepted");
            followBody.setActivity_id("");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
                long timeInMilliseconds = mDate.getTime();
                //  System.out.println("Date in milli :: " + timeInMilliseconds);
                followBody.setTime_followed(-timeInMilliseconds);
                //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            followersTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        refreshList();
                        followingTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            listener.hideProgressLoader();
                                            if(task.isSuccessful()){
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                },2000);
                                                Toasty.success(getContext(),"now following").show();
                                                listener.sendNotification("follow_unlocked",AppPreference.getCurrentUserId(),id,"");
                                            }else{
                                                Toasty.error(getContext(),task.getException().getMessage()).show();
                                            }
                                        }
                                    });
                                }else{
                                    listener.hideProgressLoader();
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else{
                        listener.hideProgressLoader();
                        Toasty.error(getContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }else{
            final FollowBody followBody = new FollowBody();
            followBody.setRequest_type("pending");
            followersTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if(task.isSuccessful()){
                                    followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                //check if the locked account already follows your account
                                                checkIfLockedAccountFollows(id,position);

                                            }else{
                                                Toasty.error(getContext(),task.getException().getMessage()).show();
                                            }
                                        }
                                    });

                                }
                                else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });

                    }else{
                        listener.hideProgressLoader();
                        Toasty.error(getContext(),task.getException().getMessage()).show();
                    }
                }
            });
        }

    }

    private void acceptRequest(final String id , final int position){
        listener.showProgressLoader();
        followersTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("activity_id").exists()){
                    followActivityKey = dataSnapshot.child("activity_id").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final FollowBody followBody = new FollowBody();
        followBody.setRequest_type("accepted");
        followBody.setActivity_id("");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date mDate = sdf.parse(TimeDateUtils.getCurrentGMTTimestamp());
            long timeInMilliseconds = mDate.getTime();
            //  System.out.println("Date in milli :: " + timeInMilliseconds);
            followBody.setTime_followed(-timeInMilliseconds);
            //Toasty.error(getContext(), TimeDateUtils.getTimeAgo(timeInMilliseconds)+"").show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        followersTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    followingTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followersTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //check if this user follows the other account
                                                        checkIfCurrentUserFollows(id,position);

                                                    }  else{
                                                        listener.hideProgressLoader();
                                                        Toasty.error(getContext(),task.getException().getMessage()).show();
                                                    }
                                                }
                                            });
                                        }else{
                                            listener.hideProgressLoader();
                                            Toasty.error(getContext(),task.getException().getMessage()).show();
                                        }
                                    }
                                });

                            }else{
                                listener.hideProgressLoader();
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });
                } else{
                    listener.hideProgressLoader();
                    Toasty.error(getContext(),task.getException().getMessage()).show();
                }
            }
        });

    }

    private void cancelRequest(final String id, final int position){
        listener.showProgressLoader();
        followersTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("activity_id").exists()){
                    followActivityKey = dataSnapshot.child("activity_id").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        followersTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.hideProgressLoader();
                if (task.isSuccessful()){
                    followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                checkIfLockedAccountFollowsYou(id,position);
                            } else{
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }else{
                    Toasty.error(getContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    private void unfollow(final String id, final int position){
        listener.showProgressLoader();
        acceptFollowRequestNotifications.child(AppPreference.getCurrentUserId()).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    removeAcceptActivityKey = ds.child("activity_id").getValue().toString();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        followersTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    followActivityKey = dataSnapshot.child("activity_id").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        followersTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.hideProgressLoader();
                if(task.isSuccessful()){
                    followingTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                //check if the user was following before
                                checkForPendingRequestInFollowingTable(id,position);

                            }else{
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }else{
                    Toasty.error(getContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    private void checkForPendingRequestInFollowingTable(final String id, final int position){
        followersTable.child(AppPreference.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").child(id).exists()){
                    FollowBody fb = new FollowBody();
                    fb.setRequest_type("follow back");
                    followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                refreshList();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(position);
                                    }
                                },2000);

                                listener.sendNotification("cancel_accept_request",id,AppPreference.getCurrentUserId(),removeAcceptActivityKey);
                                listener.sendNotification("cancel_follow_notification",AppPreference.getCurrentUserId(),id,followActivityKey);
                                Toasty.success(getContext(),"unfollowed").show();
                            }else{
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }
                else{
                    followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                refreshList();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(position);
                                    }
                                },2000);

                                listener.sendNotification("cancel_accept_request",id,AppPreference.getCurrentUserId(),removeAcceptActivityKey);
                                listener.sendNotification("cancel_follow_notification",AppPreference.getCurrentUserId(),id,followActivityKey);
                                Toasty.success(getContext(),"unfollowed").show();
                            }else{
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkIfLockedAccountFollowsYou(final String id, final int position){
        followersTable.child(AppPreference.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(id).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FollowBody fb = new FollowBody();
                                    fb.setRequest_type("follow back");
                                    followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                refreshList();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adapter.notifyItemChanged(position);
                                                    }
                                                },2000);
                                                listener.sendNotification("cancel_request",AppPreference.getCurrentUserId(),id,followActivityKey);
                                                Toasty.success(getContext(),"request cancelled").show();
                                            }
                                            else{
                                                Toasty.error(getContext(),task.getException().getMessage()).show();
                                            }
                                        }
                                    });

                                }else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else{
                        refreshList();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(position);
                            }
                        },2000);

                        listener.sendNotification("cancel_request",AppPreference.getCurrentUserId(),id,followActivityKey);
                        Toasty.success(getContext(),"request cancelled").show();
                    }
                }else{
                    refreshList();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    },2000);

                    listener.sendNotification("cancel_request",AppPreference.getCurrentUserId(),id,followActivityKey);
                    Toasty.success(getContext(),"request cancelled").show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfLockedAccountFollows(final String id, final int position){
        followersTable.child(AppPreference.getCurrentUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(id).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accept request");
                        followersTable.child(AppPreference.getCurrentUserId()).child("accepted").child(id).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    refreshList();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
                                        }
                                    },2000);


                                    listener.sendNotification("follow_locked",AppPreference.getCurrentUserId(),id,"");
                                    Toasty.success(getContext(),"request sent").show();
                                }else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else{
                        refreshList();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyItemChanged(position);
                            }
                        },2000);
                        listener.sendNotification("follow_locked",AppPreference.getCurrentUserId(),id,"");
                        Toasty.success(getContext(),"request sent").show();
                    }
                }else{
                    refreshList();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    },2000);
                    listener.sendNotification("follow_locked",AppPreference.getCurrentUserId(),id,"");
                    Toasty.success(getContext(),"request sent").show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfCurrentUserFollows(final String id, final int position){
        followersTable.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(id).child("accepted").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if(task.isSuccessful()){
                                    refreshList();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
                                        }
                                    },2000);
                                    listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),id,followActivityKey);
//                                    listener.sendNotification("cancel_request",id,AppPreference.getCurrentUserId(),followActivityKey);
                                    Toasty.success(getContext(),"request accepted").show();
                                }else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }

                            }
                        });

                    }else{
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("follow back");
                        followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if (task.isSuccessful()){
                                    refreshList();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyItemChanged(position);
                                        }
                                    },2000);
                                    listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),id,followActivityKey);
//                                    listener.sendNotification("cancel_request",id,AppPreference.getCurrentUserId(),followActivityKey);
                                    Toasty.success(getContext(),"request accepted").show();
                                }else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                }else{
                    FollowBody fb = new FollowBody();
                    fb.setRequest_type("follow back");
                    followingTable.child(id).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.hideProgressLoader();
                            if (task.isSuccessful()){
                                refreshList();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyItemChanged(position);
                                    }
                                },2000);
                                listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),id,followActivityKey);
                               // listener.sendNotification("cancel_request",id,AppPreference.getCurrentUserId(),followActivityKey);
                                Toasty.success(getContext(),"request accepted").show();
                            }else{
                                Toasty.error(getContext(),task.getException().getMessage()).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
