package com.example.ooduberu.chatapp.activities;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;

import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.model.User;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FindFriendActivity extends BaseActivity {
    Unbinder unbinder;
    DatabaseReference usersTable;
    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User,UsersViewHolder> adapter;
    Handler handler = new Handler();


    @BindView(R.id.toolbar) Toolbar search_nav_toolbar;
    @BindView(R.id.temporary_loading_view) FrameLayout temporary_loading_view;
    @BindView(R.id.users_list) RecyclerView users_list;
    @BindView(R.id.no_result_found_layout) FrameLayout no_result_found_layout;

    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(search_nav_toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uId =  AppPreference.getCurrentUserId();

        usersTable = FirebaseDatabase.getInstance().getReference().child("Users");

        users_list.setLayoutManager(new LinearLayoutManager(this));
        users_list.setHasFixedSize(true);

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
            public boolean onQueryTextSubmit(final String s) {
                if(!NetworkUtils.isNetworkAvailable(getBaseContext())){
                    Toasty.warning(getBaseContext(),"no internet connection").show();
                    return false;
                }

                if(TextUtils.isEmpty(s)){
                    temporary_loading_view.setVisibility(View.GONE);
                }else{
                    temporary_loading_view.setVisibility(View.VISIBLE);
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    findUser(s);
                                }
                            }, 2000);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                if(!NetworkUtils.isNetworkAvailable(getBaseContext())){
                    Toasty.warning(getBaseContext(),"no internet connection").show();
                    return false;
                }

                if(TextUtils.isEmpty(s)){
                    temporary_loading_view.setVisibility(View.GONE);
                }else{
                    temporary_loading_view.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findUser(s);
                        }
                    },2000);
                    findUser(s);
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void findUser(String s){
        //query the database to find the user
        Query firebaseSearchQuery = usersTable.orderByChild("user_name").startAt(s).endAt(s + "\uf8ff");

        options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

         adapter = new FirebaseRecyclerAdapter<User,UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position, @NonNull User model) {
                holder.setUserName(model.getUser_name());
                String full_name = model.getFirst_name()+" "+model.getLast_name();
                holder.setFullName(full_name);
                holder.setImage(model.getImage(),getBaseContext());

                ViewCompat.setTransitionName(holder.user_image, "profile");
                //ViewCompat.setTransitionName(holder.user_image, "profile"+position);


                holder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            viewUserProfile(getRef(position).getKey(),holder.user_image);
//                        if(getRef(position).getKey().toString().equals(uId)){
//
//                        }
                    }
                });


                //Toasty.error(getBaseContext(),getRef(position).getKey()+"").show();
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.find_user_layout,viewGroup, false);

                return new UsersViewHolder(view);
            }

             @Override
             public void onDataChanged() {
                 super.onDataChanged();

                 if(adapter.getItemCount() == 0){
                     users_list.setVisibility(View.GONE);
                     no_result_found_layout.setVisibility(View.VISIBLE);
                     temporary_loading_view.setVisibility(View.GONE);
                 }else{
                     users_list.setVisibility(View.VISIBLE);
                     no_result_found_layout.setVisibility(View.GONE);
                     temporary_loading_view.setVisibility(View.GONE);
                 }
             }
         };

         if(adapter != null){
             adapter.startListening();
             users_list.setAdapter(adapter);
         }


    }

    private void viewUserProfile(String userId,ImageView sharedImageView){
        DeviceUtils.hideKeyboard(this);
        if(userId.equals(uId)){
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, sharedImageView,  "profile");
            startActivity(intent,options.toBundle());
        }else{
            Intent intent = new Intent(getBaseContext(), FoundFriendActivity.class).putExtra("otherUsersId",userId);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, sharedImageView,  "profile");
            startActivity(intent,options.toBundle());
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();

        }
    }

    @Override
    protected void onStop() {
        if(adapter != null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();
        }
    }

    //we need to create a usersview holder class
    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View userView;//creates a view that will be used by the firebase adapter
        TextView userName;
        TextView  fullName;
        CircleImageView user_image;

        public UsersViewHolder(View itemView) {
            super(itemView);

            userView = itemView;
            user_image =(CircleImageView)userView.findViewById(R.id.user_image);
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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
