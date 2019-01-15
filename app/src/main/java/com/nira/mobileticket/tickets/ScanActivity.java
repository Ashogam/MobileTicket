package com.nira.mobileticket.tickets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nira.mobileticket.BaseActivity;
import com.nira.mobileticket.R;
import com.nira.mobileticket.Utils;
import com.nira.mobileticket.model.Transactions;
import com.nira.mobileticket.model.Wallet;
import com.nira.mobileticket.wallet.WalletActivity;

import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class ScanActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ScanActivity.class.getName();
    private Button btn_minus, btn_plus, btn_buy;
    private TextView no_of_tickets, from, to, txt_ticket_count_price, wallet_balance, txt_bus_no;
    ArrayAdapter<String> adapter;

    private String driverName, conductorName, busNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            if (getIntent().hasExtra("conductor")) {
                conductorName = getIntent().getStringExtra("conductor");
            }

            if (getIntent().hasExtra("driver")) {
                driverName = getIntent().getStringExtra("driver");
            }

            if (getIntent().hasExtra("bus_no")) {
                busNo = getIntent().getStringExtra("bus_no");
            }
        }

        setContentView(R.layout.activity_scan);
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_buy).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        no_of_tickets = findViewById(R.id.no_of_tickets);
        txt_bus_no = findViewById(R.id.txt_bus_no);
        txt_ticket_count_price = findViewById(R.id.txt_ticket_count_price);
        wallet_balance = findViewById(R.id.wallet_balance);
        checkWalletBalance();
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        from.setOnClickListener(this);
        to.setOnClickListener(this);
        loadStopsFromDatabase();
        layoutVisible();
        txt_bus_no.setText(busNo);
        adapter = new ArrayAdapter<>(ScanActivity.this, android.R.layout.select_dialog_item);
    }

    int count = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_minus:
                if (count != 0) {
                    --count;
                    no_of_tickets.setText(String.valueOf(count));
                    updateTicketCalculation();
                }
                break;
            case R.id.btn_plus:
                ++count;
                no_of_tickets.setText(String.valueOf(count));
                updateTicketCalculation();
                break;
            case R.id.btn_buy:
                initiateTransation();
                break;
            case R.id.from:
                callFrom();
                break;
            case R.id.to:
                callTo();
                break;
        }
    }


    void initiateTransation() {
        if (wallet != null && wallet.getAmount() >= 0 && wallet.getAmount() >= Integer.valueOf(totalFare)) {
            if (!TextUtils.isEmpty(fromValue) && !TextUtils.isEmpty(toValue))
                updateMoney();
            else
                showPopUpAlert("Alert", "Enter all fields");
        } else {
            showPopUpAlert("Alert", "Balance is insufficient. Add money in to wallet", WalletActivity.class);
        }
    }


    void finishTransation() {
        final Transactions transactions = new Transactions();
        transactions.setLocomotive(fromValue + " to " + toValue);
        transactions.setFare(busFare);
        transactions.setTotalFare(String.valueOf(totalFare));
        transactions.setnTickets(no_of_tickets.getText().toString());
        transactions.setStatus("Success");
        transactions.setBus_no(busNo);
        transactions.setValidity(String.valueOf(new Date().getTime()));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(FirebaseAuth.getInstance().getUid())
                .push();
        ref.setValue(transactions, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showDialog(transactions);
                } else {
                    Toast.makeText(ScanActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void callFrom() {
        adapter.clear();
        to.setText(null);
        if (stops != null && stops.size() > 0) {
            Iterator<String> val = stops.iterator();
            while (val.hasNext()) {
                adapter.add(val.next());
            }
            showPopUp("From Location", adapter, true);
        } else
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
    }


    void callTo() {
        if (!TextUtils.isEmpty(from.getText())) {
            adapter.clear();
            if (stops != null && stops.size() > 0) {
                Iterator<String> val = stops.iterator();
                while (val.hasNext()) {
                    String value = val.next();
                    if (!fromValue.equals(value))
                        adapter.add(value);
                }
                showPopUp("From Location", adapter, false);
            } else
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Please select from location", Toast.LENGTH_SHORT).show();
        }
    }

    void layoutVisible() {
        if (!TextUtils.isEmpty(fromValue) && !TextUtils.isEmpty(toValue)) {
            findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
            loadPrice();
        } else
            findViewById(R.id.bottom_layout).setVisibility(View.GONE);
    }


    String fromValue, toValue;

    void showPopUp(String title, final ArrayAdapter<String> adapter, final Boolean isFrom) {
        AlertDialog.Builder popUp = new AlertDialog.Builder(this);
        popUp.setTitle(title);
        popUp.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        popUp.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String itemName = adapter.getItem(which);
                if (isFrom) {
                    fromValue = itemName;
                    from.setText(fromValue);
                } else {
                    toValue = itemName;
                    to.setText(toValue);
                }
                layoutVisible();
                Toast.makeText(ScanActivity.this, "Selected Item " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
        popUp.show();
    }


    TreeSet<String> stops = new TreeSet<>();

    void loadStopsFromDatabase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Stops");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    stops.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ScanActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String busFare;

    void loadPrice() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("Prices").child(fromValue.toUpperCase() + " TO " + toValue.toUpperCase());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    busFare = String.valueOf(dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    String content = "Number of ticket";
    int totalFare;

    void updateTicketCalculation() {
        totalFare = Integer.parseInt(no_of_tickets.getText().toString()) * Integer.parseInt(busFare);
        txt_ticket_count_price.setText(content + " - " + no_of_tickets.getText().toString() + "\n\nBus Fare - Rs. " + busFare + "\n\nTotal price - Rs. " + totalFare);
    }


    int walletAmount;
    Wallet wallet;

    void checkWalletBalance() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet = dataSnapshot.getValue(Wallet.class);
                if (wallet != null) {
                    walletAmount = wallet.getAmount();
                    Log.i(TAG, wallet.getAmount() + "");
                    wallet_balance.setText("Available wallet balance is " + walletAmount);
                } else {
                    wallet = new Wallet();
                    wallet.setLocked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //TODO Final Transaction Receipt
    public void showDialog(Transactions transactions) {
        TextView passenger_name, locomotive, n_tickets, validity, fare, total_fare, current_amount, receipt_bus_details;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_transaction);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        passenger_name = dialog.findViewById(R.id.passenger_name);
        receipt_bus_details = dialog.findViewById(R.id.receipt_bus_details);
        locomotive = dialog.findViewById(R.id.locomotive);
        n_tickets = dialog.findViewById(R.id.n_tickets);
        validity = dialog.findViewById(R.id.validity);
        fare = dialog.findViewById(R.id.fare);
        total_fare = dialog.findViewById(R.id.total_fare);
        current_amount = dialog.findViewById(R.id.current_amount);

        if (transactions != null) {
            passenger_name.setText(getUserProfile("name"));
            locomotive.setText(transactions.getLocomotive());
            n_tickets.setText(transactions.getnTickets());
            validity.setText(Utils.convertTime(transactions.getValidity()));
            fare.setText(transactions.getFare());
            total_fare.setText(transactions.getTotalFare());
            current_amount.setText("Wallet Balance Rs. " + val);
            receipt_bus_details.setText(busNo + " - " + driverName + " , " + conductorName);
        }

        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();

    }

    int val;

    void updateMoney() {
        showProgessBar();
        val = wallet.getAmount() - totalFare;
        wallet.setAmount(val);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getUid());
        ref.setValue(wallet, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressBar();
                if (databaseError == null) {
                    finishTransation();
                } else {
                    Toast.makeText(ScanActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
