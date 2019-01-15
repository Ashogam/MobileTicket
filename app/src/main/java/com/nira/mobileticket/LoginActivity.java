package com.nira.mobileticket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nira.mobileticket.model.User;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getName();
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;


    private TextInputLayout txt_number, txt_otp;
    private Button btn_login;


    void changeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser() != null) {
            checkWhetherThePicIsUpdatedOrNot();
        }

        mAuth.useAppLanguage();

        txt_number = findViewById(R.id.txt_number);
        txt_otp = findViewById(R.id.txt_otp);
        btn_login = findViewById(R.id.btn_login);

        txt_otp.setVisibility(View.GONE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardFrom();
                if (txt_otp.getVisibility() == View.VISIBLE) {
                    if (!TextUtils.isEmpty(txt_otp.getEditText().getText())) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, txt_otp.getEditText().getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter the otp", Toast.LENGTH_SHORT).show();
                    }
                } else if (!TextUtils.isEmpty(txt_number.getEditText().getText()))
                    verifyNumber(txt_number.getEditText().getText().toString());
                else
                    Toast.makeText(LoginActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void verifyNumber(String mobileNumber) {
        Utils.showProgessBar(getWindow().getDecorView().findViewById(android.R.id.content));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            Toast.makeText(LoginActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            txt_otp.setVisibility(View.VISIBLE);
            btn_login.setText("Continue");
            Log.d(TAG, "onCodeSent:" + verificationId);
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            Utils.hideProgressBar(getWindow().getDecorView().findViewById(android.R.id.content));

            // ...
        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Utils.showProgessBar(getWindow().getDecorView().findViewById(android.R.id.content));
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Utils.hideProgressBar(getWindow().getDecorView().findViewById(android.R.id.content));
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            checkWhetherThePicIsUpdatedOrNot();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    void checkWhetherThePicIsUpdatedOrNot() {
        Utils.showProgessBar(getWindow().getDecorView().findViewById(android.R.id.content));
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByKey().equalTo(FirebaseAuth.getInstance().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User data = null;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    data = child.getValue(User.class);
                }
                if (data == null) {
                    Toast.makeText(LoginActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                    changeScreen();
                    finish();
                } else {
                    setUserProfile(data.displayName, data.profilePicURL);
                    Toast.makeText(LoginActivity.this, "Data is available", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
