package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseAuth mAuth;

    @BindView(R.id.app_navigate) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setTitle("chat-app");

        mAuth = FirebaseAuth.getInstance();

        Toasty.success(getBaseContext(), AppPreference.getCurrentUserId()).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.findFriendsLink:
                startActivity(new Intent(getBaseContext(),FindFriendActivity.class));
                break;

            case R.id.settings_link:
                startActivity(new Intent(getBaseContext(),SettingsActivity.class));
                break;

            case R.id.log_out_link:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.sign_out)
    public void signOut(){
        mAuth.signOut();
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
