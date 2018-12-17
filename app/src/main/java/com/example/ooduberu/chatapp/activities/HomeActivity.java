package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        Toasty.success(getBaseContext(), AppPreference.getCurrentUserId()).show();
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
