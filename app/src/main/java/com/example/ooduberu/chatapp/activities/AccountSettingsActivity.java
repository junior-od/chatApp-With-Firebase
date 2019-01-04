package com.example.ooduberu.chatapp.activities;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

public class AccountSettingsActivity extends BaseActivity {
    Unbinder unbinder;
    DatabaseReference userTable;
    Switch private_account_switch;

    @BindView(R.id.app_navigate) Toolbar mToolbar;

    String uId;
    String accountType;

    boolean accountTypeSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);//sets the action bar for the activity
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uId = AppPreference.getCurrentUserId();

        private_account_switch = (Switch)findViewById(R.id.private_account_switch);


        userTable = FirebaseDatabase.getInstance().getReference().child("Users");
        userTable.keepSynced(true);

        userTable.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                accountType = dataSnapshot.child("account_type").getValue().toString();
                if(accountType.equalsIgnoreCase("locked")){
                    private_account_switch.setChecked(true);
                }else{
                    private_account_switch.setChecked(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        private_account_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                privateSwitchChange(isChecked);
            }
        });

    }


    private void privateSwitchChange(boolean checked){
        accountTypeSwitch = checked;


        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.change_privacy_popup, null);
        TextView title = (TextView)mView.findViewById(R.id.title);
        TextView message = (TextView)mView.findViewById(R.id.message);
        ConstraintLayout ok_layout = (ConstraintLayout)mView.findViewById(R.id.ok_layout);
        ConstraintLayout cancel_layout = (ConstraintLayout)mView.findViewById(R.id.cancel_layout);


        mBuilder.setView(mView);
        final AlertDialog dialog  = mBuilder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        cancel_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                private_account_switch.setChecked(!accountTypeSwitch);
                dialog.cancel();
            }
        });

        if (accountTypeSwitch){

            title.setText("Change to private account ?");
            message.setText("When your account is private, only people you approve can see your feeds on here. Your existing followers won't be affected.");
            ok_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressLoader();

                    userTable.child(uId).child("account_type").setValue("locked").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressLoader();
                            if(task.isSuccessful()){
                                dialog.cancel();
                            }else{
                                private_account_switch.setChecked(!accountTypeSwitch);
                                dialog.cancel();
                            }
                        }
                    });


                }
            });
        }else{
            title.setText("Change to public account ?");
            message.setText("Anyone will be able to see your feeds. You will no longer need to approve followers.");
            ok_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressLoader();

                    userTable.child(uId).child("account_type").setValue("unlocked").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressLoader();
                            if(task.isSuccessful()){
                                dialog.cancel();
                            }else{
                                private_account_switch.setChecked(!accountTypeSwitch);
                                dialog.cancel();
                            }
                        }
                    });
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
