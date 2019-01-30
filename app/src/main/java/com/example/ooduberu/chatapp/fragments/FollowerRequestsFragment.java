package com.example.ooduberu.chatapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.activities.FoundFriendActivity;
import com.example.ooduberu.chatapp.activities.ProfileActivity;
import com.example.ooduberu.chatapp.interfaces.Listener;
import com.example.ooduberu.chatapp.model.FollowBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowerRequestsFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;
    Listener listener;
    Activity myActivity;

    DatabaseReference userTable;
    DatabaseReference followersTablePending;
    DatabaseReference followersTable;
    DatabaseReference followingTable;

    Query query;
    FirebaseRecyclerOptions<FollowBody> options;
    FirebaseRecyclerAdapter<FollowBody,FollowersRequestViewHolder> adapter;

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
        followersTablePending = FirebaseDatabase.getInstance().getReference().child("followers").child(AppPreference.getCurrentUserId()).child("pending");
        followersTable = FirebaseDatabase.getInstance().getReference().child("followers");
        followingTable = FirebaseDatabase.getInstance().getReference().child("following");

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
                        .setIndexedQuery(query,followersTablePending,FollowBody.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FollowBody, FollowersRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FollowersRequestViewHolder holder, final int position, @NonNull FollowBody model) {
                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUserProfile(getRef(position).getKey(),holder.user_image);
                    }
                });

                holder.accept_request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptRequest(getRef(position).getKey());
                    }
                });

                holder.cancel_request_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelRequest(getRef(position).getKey());
                    }
                });

                userTable.child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(myActivity == null){
                            return;
                        }
                        holder.setFullName(dataSnapshot.child("first_name").getValue().toString()+" "+dataSnapshot.child("last_name").getValue().toString());
                        holder.setUserName(dataSnapshot.child("user_name").getValue().toString());
                        holder.setImage(dataSnapshot.child("image").getValue().toString(),myActivity);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FollowersRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_pending_follower_request,viewGroup, false);

                return new FollowersRequestViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(adapter.getItemCount() == 0){
                    no_requests_layout.setVisibility(View.VISIBLE);
                    followers_request_recycler_view.setVisibility(View.GONE);

                }else{
                    no_requests_layout.setVisibility(View.GONE);
                    followers_request_recycler_view.setVisibility(View.VISIBLE);
                }

            }
        };

        if(adapter != null){
            followers_request_recycler_view.setAdapter(adapter);
            adapter.startListening();
        }

    }

    public static class FollowersRequestViewHolder extends RecyclerView.ViewHolder{
        View userView;//creates a view that will be used by the firebase adapter
        TextView userName;
        TextView  fullName;
        CircleImageView user_image;
        ImageView accept_request_button;
        ImageView cancel_request_button;

        public FollowersRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userView = itemView;
            user_image = (CircleImageView)userView.findViewById(R.id.user_image);
            accept_request_button = (ImageView) userView.findViewById(R.id.accept_request_button);
            cancel_request_button = (ImageView) userView.findViewById(R.id.cancel_request_button);
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
//
//        public void setAccept_request_buttonButton(String text){
//            follow_button.setText(text);
//        }
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

    private void acceptRequest(final String otherUsersId){
        listener.showProgressLoader();
        final FollowBody followBody = new FollowBody();
        followBody.setRequest_type("accepted");
        followBody.setUser_name(AppPreference.getCurrentUserName());
        followersTable.child(AppPreference.getCurrentUserId()).child("accepted").child(otherUsersId).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    followingTable.child(otherUsersId).child("accepted").child(AppPreference.getCurrentUserId()).setValue(followBody).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                followersTable.child(AppPreference.getCurrentUserId()).child("pending").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            followingTable.child(otherUsersId).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        //check if this user follows the other account
                                                        checkIfCurrentUserFollows(otherUsersId);

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

    private void cancelRequest(final String otherUsersId){
        listener.showProgressLoader();
        followersTable.child(AppPreference.getCurrentUserId()).child("pending").child(otherUsersId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.hideProgressLoader();
                if (task.isSuccessful()){
                    followingTable.child(otherUsersId).child("pending").child(AppPreference.getCurrentUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                checkIfLockedAccountFollowsYou(otherUsersId);
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


    private void checkIfCurrentUserFollows(final String otherUsersId){
        followersTable.child(otherUsersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(otherUsersId).child("accepted").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if(task.isSuccessful()){
//                                    fetchFollowers();
//                                    fetchFollowing();
                                    listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),otherUsersId);
                                    listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
                                    Toasty.success(getContext(),"request accepted").show();
                                }else{
                                    Toasty.error(getContext(),task.getException().getMessage()).show();
                                }

                            }
                        });

                    }else{
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("follow back");
                        followingTable.child(otherUsersId).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.hideProgressLoader();
                                if (task.isSuccessful()){
//                                    fetchFollowers();
//                                    fetchFollowing();
                                    listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),otherUsersId);
                                    listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
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
                    followingTable.child(otherUsersId).child("pending").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.hideProgressLoader();
                            if (task.isSuccessful()){
//                                fetchFollowers();
//                                fetchFollowing();
                                listener.sendNotification("accept_follow_request",AppPreference.getCurrentUserId(),otherUsersId);
                                listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
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

    private void checkIfLockedAccountFollowsYou(final String otherUsersId){
        followersTable.child(otherUsersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("accepted").exists()){
                    if(dataSnapshot.child("accepted").child(AppPreference.getCurrentUserId()).exists()){
                        FollowBody fb = new FollowBody();
                        fb.setRequest_type("accepted");
                        followersTable.child(otherUsersId).child("accepted").child(AppPreference.getCurrentUserId()).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FollowBody fb = new FollowBody();
                                    fb.setRequest_type("follow back");
                                    followingTable.child(AppPreference.getCurrentUserId()).child("pending").child(otherUsersId).setValue(fb).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
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
                        listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
                        Toasty.success(getContext(),"request cancelled").show();
                    }
                }else{
                    listener.sendNotification("cancel_request",otherUsersId,AppPreference.getCurrentUserId());
                    Toasty.success(getContext(),"request cancelled").show();
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
