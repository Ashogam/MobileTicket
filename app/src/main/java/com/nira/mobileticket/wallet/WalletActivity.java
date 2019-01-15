package com.nira.mobileticket.wallet;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nira.mobileticket.BaseActivity;
import com.nira.mobileticket.R;
import com.nira.mobileticket.model.Wallet;
import com.nira.mobileticket.model.WalletTransactions;

import java.util.Date;
import java.util.Random;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    EditText txt_amount;
    RecyclerView recyclerView;
    TextView current_wallet_amount, title;
    WalletAdapter adapter = new WalletAdapter();
    LinearLayout input_fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        recyclerView = findViewById(R.id.recycler_view);
        input_fields = findViewById(R.id.input_fields);
        txt_amount = findViewById(R.id.txt_amount);
        title = findViewById(R.id.title);
        current_wallet_amount = findViewById(R.id.current_wallet_amount);
        findViewById(R.id.btn_100).setOnClickListener(this);
        findViewById(R.id.btn_100).setOnClickListener(this);
        findViewById(R.id.btn_200).setOnClickListener(this);
        findViewById(R.id.btn_300).setOnClickListener(this);
        findViewById(R.id.btn_300).setOnClickListener(this);
        findViewById(R.id.btn_400).setOnClickListener(this);
        Button buyAction = findViewById(R.id.btn_add_money);
        buyAction.setOnClickListener(this);
        setUpList();
        addMoney();
        getHistory();

        if (getIntent() != null && getIntent().hasExtra("check_balance")) {
            input_fields.setVisibility(View.GONE);
            buyAction.setVisibility(View.GONE);
            title.setText("My Balance");
        }

    }

    void setUpList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_100:
                txt_amount.setText("100");
                break;
            case R.id.btn_200:
                txt_amount.setText("200");
                break;
            case R.id.btn_300:
                txt_amount.setText("300");
                break;
            case R.id.btn_400:
                txt_amount.setText("400");
                break;
            case R.id.btn_add_money:
                if (!TextUtils.isEmpty(txt_amount.getText())) {
                    addHistory();
                } else {
                    Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Wallet wallet;


    void addMoney() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet = dataSnapshot.getValue(Wallet.class);
                if (wallet != null) {
                    current_wallet_amount.setText("Current balance\n Rs. " + String.valueOf(wallet.getAmount()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void updateMoney() {
        showProgessBar();
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setLocked(false);
        }
        int val = Integer.parseInt(txt_amount.getText().toString()) + wallet.getAmount();
        wallet.setAmount(val);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getUid());
        ref.setValue(wallet, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressBar();
                if (databaseError == null) {
                    showPopUp("Amount added successfully. Current balance is " + wallet.getAmount());
                    Toast.makeText(WalletActivity.this, "Amount added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WalletActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void showPopUp(String message) {
        new AlertDialog.Builder(this).setTitle("Transaction success").setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).show();
    }


    void addHistory() {
        showProgessBar();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet_Transactions").child(FirebaseAuth.getInstance().getUid()).push();
        final WalletTransactions transactions = new WalletTransactions();
        transactions.setAmount(txt_amount.getText().toString());
        transactions.setCreatedDate(String.valueOf(new Date().getTime()));
        transactions.setStatus("processing");
        transactions.setTxnID("TXN" + new Random().nextInt(100000));
        ref.setValue(transactions, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                hideProgressBar();
                if (databaseError == null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet_Transactions").child(FirebaseAuth.getInstance().getUid()).child(databaseReference.getKey());
                    transactions.setStatus("success");
                    ref.setValue(transactions, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null)
                                updateMoney();
                            else
                                Toast.makeText(WalletActivity.this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(WalletActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    void getHistory() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Wallet_Transactions").child(FirebaseAuth.getInstance().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hideProgressBar();
                adapter.transactions.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue() != null) {
                        adapter.transactions.add(child.getValue(WalletTransactions.class));
                    }
                }
                adapter.setDataAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WalletActivity.this, "Error " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
