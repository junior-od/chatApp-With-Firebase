package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.transition.PatternPathMotion;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.model.User;
import com.example.ooduberu.chatapp.utility.NetworkUtils;
import com.example.ooduberu.chatapp.utility.ValidationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SignUpActivity extends BaseActivity {
    Unbinder unbinder;
    FirebaseDatabase database;//initiate firebase database
    FirebaseAuth mAuth;
    DatabaseReference database_ref; // initiate the database reference
    FirebaseUser getCurrentUser;

    @BindView(R.id.first_name_input) TextInputEditText first_name_input;
    @BindView(R.id.last_name_input) TextInputEditText last_name_input;
    @BindView(R.id.user_name_input) TextInputEditText user_name_input;
    @BindView(R.id.email_input) TextInputEditText email_input;
    @BindView(R.id.password_input) TextInputEditText password_input;
    @BindView(R.id.confirm_password_input) TextInputEditText confirm_password_input;

    String first_name;
    String last_name;
    String user_name;
    String email;
    String password;
    String confirm_password;
    String getTokenDevice;
    String uId;
    String default_status = "Hi there I'm using fornite-connect app";

    boolean usernameExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        getTokenDevice = FirebaseInstanceId.getInstance().getId();
        database = FirebaseDatabase.getInstance();//gets the instance of the firedatabase
    }

    @OnClick(R.id.log_in)
    public void logInPage(){
        startActivity(new Intent(getBaseContext(),LoginActivity.class));
        finish();
    }


    @OnClick(R.id.button_login)
    public void signUp(){
        if(!NetworkUtils.isNetworkAvailable(this)){
            Toasty.warning(getBaseContext(),"no internet connection").show();
            return;
        }

        if(!isValidate()){
            return;

        } else if(isUsernameExists()){
           Toasty.error(getBaseContext(),"username has already been taken by another user").show();
           return;
        }
        else{
            signUpUser();
        }

    }

    private boolean isValidate(){
        first_name = first_name_input.getText().toString().trim();
        last_name = last_name_input.getText().toString().trim();
        user_name = user_name_input.getText().toString().trim();
        email = email_input.getText().toString().trim();
        password = password_input.getText().toString().trim();
        confirm_password = confirm_password_input.getText().toString().trim();

        if(((TextUtils.isEmpty(first_name) || TextUtils.isEmpty(last_name))||(TextUtils.isEmpty(user_name)||TextUtils.isEmpty(email)))||(TextUtils.isEmpty(password)||TextUtils.isEmpty(confirm_password))){
            Toasty.error(getBaseContext(),"no field must be empty").show();
            return false;

        } else if(!ValidationUtils.validateName(first_name)){
            Toasty.error(getBaseContext(),"first name : invalid name format").show();
            return false;

        } else if(!ValidationUtils.validateName(last_name)){
            Toasty.error(getBaseContext(),"last name : invalid name format").show();
            return false;

        } else if(!ValidationUtils.validateUsername(user_name)){
            Toasty.error(getBaseContext(),"user name : invalid user name format").show();
            return false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toasty.error(getBaseContext(),"invalid email format").show();
            return false;

        }else if(password.length() < 8){
            Toasty.error(getBaseContext(),"password must be minimum of 8 characters").show();
            return false;

        }else if(!ValidationUtils.complexPassword(password)){
            Toasty.error(getBaseContext(),"password must have at least one capital letter, one small letter and one number").show();
            return false;

        } else if(!password.equals(confirm_password)){
            Toasty.error(getBaseContext(),"password mismatch").show();
            return false;
        }

        return true;
    }

    private boolean isUsernameExists(){
        database_ref = database.getReference();
        database_ref.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    User users = ds.getValue(User.class);//gets the object value from the user class

                    //check if the user name already exists
                    if(user_name.equals(users.getUser_name())){
                        usernameExists = true;
                        break;
                    }
                        usernameExists = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return usernameExists;
    }

    private void signUpUser(){
        showProgressLoader();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user_information = new User();
                    user_information.setFirst_name(first_name);
                    user_information.setLast_name(last_name);
                    user_information.setUser_name(user_name);
                    user_information.setDevice_token(getTokenDevice);
                    user_information.setEmail(email);
                    user_information.setStatus(default_status);
                    user_information.setImage("default");
                    user_information.setChat_background_image("default");

                    getCurrentUser = FirebaseAuth.getInstance().getCurrentUser();//gets the current user
                    uId = getCurrentUser.getUid();
                    database_ref = database.getReference().child("Users").child(uId);
                    database_ref.setValue(user_information);//pushes the object date of user information in the User data table in the database


                    usernameExists = false;
                    Toasty.success(getBaseContext(),"sign up was sucessful").show();
                    hideProgressLoader();
                    startActivity(new Intent(getBaseContext(),HomeActivity.class));
                    finish();

                }else {
                    Toasty.error(getBaseContext(),task.getException().getMessage()).show();
                    hideProgressLoader();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
