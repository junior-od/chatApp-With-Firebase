package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class ForgotPasswordActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseAuth mAuth;

    @BindView(R.id.email_input) TextInputEditText email_input;

    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }


    @OnClick(R.id.button_resend_password)
    public void resendPasswordToEmail(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }

        if(!validate()){
            return;
        }
        else{
            sendPasswordReset();
        }

    }

    private boolean validate(){
        email = email_input.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toasty.error(getBaseContext(),"email is required").show();
            return false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toasty.error(getBaseContext(),"invalid email format").show();
            return false;

        }

        return true;
    }

    private void sendPasswordReset(){
        showProgressLoader();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressLoader();
                if(task.isSuccessful()){
                    Toasty.success(getBaseContext(),"A mail has been sent to reset your password").show();
                }else{
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    @OnClick(R.id.back_to_login)
    public void backToLogin(){
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
