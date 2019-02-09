package com.example.ooduberu.chatapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.ooduberu.chatapp.model.ActivitiesBody;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
import com.example.ooduberu.chatapp.utility.TimeDateUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserActivitiesFragment extends android.support.v4.app.Fragment {
    Unbinder unbinder;
    Activity myActivity;
    DatabaseReference activities;
    DatabaseReference userTable;
    FirebaseRecyclerOptions<ActivitiesBody> options;
    FirebaseRecyclerAdapter<ActivitiesBody,ActivitiesViewHolder> adapter;

    @BindView(R.id.no_activity_layout) FrameLayout no_activity_layout;
    @BindView(R.id.activity_recycler_view) RecyclerView activity_recycler_view;

    public UserActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myActivity = null;
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_activities, container, false);
        unbinder = ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activities = FirebaseDatabase.getInstance().getReference().child("activities").child(AppPreference.getCurrentUserId());
        userTable = FirebaseDatabase.getInstance().getReference().child("Users");

        activity_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        activity_recycler_view.setHasFixedSize(true);

        displayUsersActivites();


    }


    private void displayUsersActivites(){
        //todo query by time created
        Query query = activities.orderByKey();

        options =
                new FirebaseRecyclerOptions.Builder<ActivitiesBody>()
                        .setQuery(query,ActivitiesBody.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ActivitiesBody, ActivitiesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ActivitiesViewHolder holder, int position, @NonNull final ActivitiesBody model) {

                if((model.getType().equalsIgnoreCase("new follower") || model.getType().equalsIgnoreCase("follower request"))|| model.getType().equalsIgnoreCase("accepted request")){
                    holder.post_image.setVisibility(View.GONE);
                    holder.userView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewUserProfile(model.getUser_id(),holder.user_image);


                        }
                    });

                }

                holder.setMessage(model.getMessage());
                if(!model.getTime().isEmpty()){
                    holder.setTime(TimeDateUtils.getTimeAgo(Long.parseLong(model.getTime())));
                }

                userTable.child(model.getUser_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(myActivity == null){
                            return;
                        }
                        holder.setUserName(dataSnapshot.child("user_name").getValue().toString());
                        holder.setUserImage(dataSnapshot.child("image").getValue().toString(),myActivity);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_activity_layout,viewGroup,false);

                return new ActivitiesViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(adapter.getItemCount() == 0){
                    no_activity_layout.setVisibility(View.VISIBLE);
                    activity_recycler_view.setVisibility(View.GONE);

                }else{
                    no_activity_layout.setVisibility(View.GONE);
                    activity_recycler_view.setVisibility(View.VISIBLE);
                }
            }
        };

        if(myActivity != null){
            activity_recycler_view.setAdapter(adapter);
            adapter.startListening();
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


    public static class ActivitiesViewHolder extends RecyclerView.ViewHolder{
        View userView;//creates a view that will be used by the firebase adapter
        TextView userName;
        TextView  message;
        TextView time;
        CircleImageView user_image;
        ImageView post_image;

        public ActivitiesViewHolder(@NonNull View itemView) {
            super(itemView);

            userView = itemView;
            user_image = (CircleImageView)userView.findViewById(R.id.user_image);
            post_image = (ImageView) userView.findViewById(R.id.post_image);
        }


        //creates a method to set the username of the user
        public void setUserName(String name){
            userName = (TextView)userView.findViewById(R.id.user_name);
            userName.setText(name);
        }

        //creates a method to set message
        public void setMessage(String msg){
             message= (TextView)userView.findViewById(R.id.message);
             message.setText(msg);
        }

        public void setTime(String t){
            time = (TextView)userView.findViewById(R.id.time);
            time.setText(t);
        }

        //sets the image of the user
        public void setUserImage(String image, Context context){
            if (image.equals("default")){
                user_image.setImageResource(R.drawable.person_placeholder);
            }
            else{
                Glide.with(context).load(image)
                        .apply(new RequestOptions().error(R.drawable.person_placeholder).placeholder(R.drawable.person_placeholder).fitCenter())
                        .into(user_image);
            }
        }

        public void setPostImage(String image, Context context){
            Glide.with(context).load(image).apply(new RequestOptions().error(R.drawable.white_background_placeholder).placeholder(R.drawable.white_background_placeholder).fitCenter())
                    .into(post_image);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
