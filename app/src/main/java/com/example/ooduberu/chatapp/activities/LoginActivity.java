package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.example.ooduberu.chatapp.utility.DeviceUtils;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.example.ooduberu.chatapp.utility.ValidationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference users_table;

    @BindView(R.id.login_email) TextInputEditText login_email;
    @BindView(R.id.login_password) TextInputEditText login_password;
    @BindView(R.id.delete_password_input) FrameLayout delete_password_input;
    @BindView(R.id.button_login) MaterialButton button_login;

    String userEmail;
    String userPassword;
    String getDeviceToken;
    String getUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        users_table = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){ //checks if the last user is still logged in
                    AppPreference.setCurrentUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(new Intent(getBaseContext(),HomeActivity.class));
                    finish();
                }
            }
        };
    }

    @Override// whenever this page start it runs this function
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private boolean isValidate(){
        userEmail = login_email.getText().toString().trim();
        userPassword = login_password.getText().toString().trim();

        if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)){
            Toasty.error(getBaseContext(),"email / password field is empty ").show();
            return false;

        } else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            Toasty.error(getBaseContext(),"not a valid email format").show();
            return false;

        } else if(userPassword.length() < 8){
            Toasty.error(getBaseContext(),"email or password is invalid").show();
            return false;

        } else if(!ValidationUtils.complexPassword(userPassword)){
            Toasty.error(getBaseContext(),"email or password is invalid").show();
            return false;
        }

        return true;

    }

    @OnClick(R.id.button_login)
    public void loginToApp(){
        DeviceUtils.hideKeyboard(this);
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }

        if(!isValidate()){
            return;
        }else{
            logIn();
        }

    }

    //log in the user with email and password
    private void logIn(){
        showProgressLoader();
        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getDeviceToken = FirebaseInstanceId.getInstance().getToken();
                    AppPreference.setCurrentUserId(mAuth.getCurrentUser().getUid());

                    //incase a user logs on on a new device replace old device token with new device token
                    users_table.child(AppPreference.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            hideProgressLoader();
                            if(task.isSuccessful()){
                                AppPreference.setCurrentUserName(dataSnapshot.child("user_name").getValue().toString());
                                users_table.child(AppPreference.getCurrentUserId()).child("device_token").setValue(getDeviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(getBaseContext(),HomeActivity.class));
                                            finish();
                                        }else{
                                            Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                                        }
                                    }
                                });
                            }
                            else{
                                Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    hideProgressLoader();
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                }
            }
        });
    }

    @OnClick(R.id.delete_password_input)
    public void clearPasswordField(){

    }


    @OnClick(R.id.sign_up)
    public void signUpPage(){
        DeviceUtils.hideKeyboard(this);
        startActivity(new Intent(getBaseContext(),SignUpActivity.class));
    }

    @OnClick(R.id.forgot_password_link)
    public void forgotPasswordPage(){
        DeviceUtils.hideKeyboard(this);
        startActivity(new Intent(getBaseContext(),ForgotPasswordActivity.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
