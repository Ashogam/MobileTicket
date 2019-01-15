package com.nira.mobileticket.profile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nira.mobileticket.BaseActivity;
import com.nira.mobileticket.LoginActivity;
import com.nira.mobileticket.R;
import com.nira.mobileticket.model.Wallet;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends BaseActivity {

    TextView wallet_amount, profile_name;
    CircleImageView profile_image;
    ImageView profile_pic_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        wallet_amount = findViewById(R.id.wallet_amount);
        profile_name = findViewById(R.id.profile_name);
        profile_image = findViewById(R.id.profile_image);
        profile_pic_bg = findViewById(R.id.profile_pic_bg);
        profile_name.setText(getUserProfile("name"));
        findViewById(R.id.btn_log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Profile.this, "Signing Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Profile.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        getBalance();
        loadImage();
    }


    void loadImage() {
        try {
            if (getUserProfile("profilePic") != null) {
                Picasso.get().load(getUserProfile("profilePic")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(profile_image);
                Picasso.get().load(getUserProfile("profilePic")).placeholder(R.drawable.bg_two).error(R.drawable.bg_two).into(profile_pic_bg);
            } else Toast.makeText(this, "Failed to load profile pic", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load profile pic", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    Wallet wallet;

    void getBalance() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet = dataSnapshot.getValue(Wallet.class);
                if (wallet != null) {
                    wallet_amount.setText("Current balance\n Rs. " + String.valueOf(wallet.getAmount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
