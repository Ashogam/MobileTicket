package com.nira.mobileticket;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.nira.mobileticket.model.Vehicle;
import com.nira.mobileticket.profile.ContactUs;
import com.nira.mobileticket.profile.Profile;
import com.nira.mobileticket.tickets.ScanActivity;
import com.nira.mobileticket.tickets.TransactionHistory;
import com.nira.mobileticket.wallet.WalletActivity;

public class DashboardActivity extends BaseActivity implements View.OnClickListener {

    TextView welcome_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        welcome_text = findViewById(R.id.welcome_text);
        welcome_text.setText("Welcome " + getUserProfile("name"));
        findViewById(R.id.buy_ticket).setOnClickListener(this);
        findViewById(R.id.ticket_history).setOnClickListener(this);
        findViewById(R.id.wallet).setOnClickListener(this);
        findViewById(R.id.wallet_balance).setOnClickListener(this);
        findViewById(R.id.profile).setOnClickListener(this);
        findViewById(R.id.contact_us).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_ticket:
                openCamera();
                break;
            case R.id.ticket_history:
                startActivity(new Intent(DashboardActivity.this, TransactionHistory.class));
                break;

            case R.id.wallet:
                startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
                break;

            case R.id.wallet_balance:
                Intent intent = new Intent(DashboardActivity.this, WalletActivity.class);
                intent.putExtra("check_balance", "true");
                startActivity(intent);
                break;

            case R.id.profile:
                startActivity(new Intent(DashboardActivity.this, Profile.class));
                break;

            case R.id.contact_us:
                startActivity(new Intent(DashboardActivity.this, ContactUs.class));
                break;
        }
    }


    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                getVehicleDetails(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    void getVehicleDetails(final String key) {
        showProgessBar();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Vehicle").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideProgressBar();
                final Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                if (vehicle != null)
                    showPopUpAlert(key, "Your vehicle details are, Driver Name " + vehicle.getDriver_name() + " and the Conductor name " + vehicle.getConductor_name(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(DashboardActivity.this, ScanActivity.class);
                            intent.putExtra("conductor", vehicle.getConductor_name());
                            intent.putExtra("driver", vehicle.getDriver_name());
                            intent.putExtra("bus_no", key);
                            startActivity(intent);
                        }
                    });
                else
                    showPopUpAlert("Error", "Invalid Vehicle, please rescan and process your tickets");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressBar();
                showPopUpAlert("Error", "Invalid Vehicle, please rescan and process your tickets");
            }
        });
    }

    void openCamera() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
}
