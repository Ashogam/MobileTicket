package com.nira.mobileticket.tickets;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nira.mobileticket.R;
import com.nira.mobileticket.model.Transactions;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private static final String TAG = TransactionHistory.class.getName();
    List<Transactions> transactions = new ArrayList<>();
    TransactionAdapter adapter = new TransactionAdapter(transactions);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        RecyclerView view = findViewById(R.id.recycler_view);
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        view.setAdapter(adapter);
        if (transactions != null && transactions.size() <= 0) {
            callTransaction();
        }
    }


    void callTransaction() {
        Query ref = FirebaseDatabase.getInstance().getReference().child("Transactions").child(FirebaseAuth.getInstance().getUid()).orderByKey();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getValue(Transactions.class) != null) {
                        transactions.add(child.getValue(Transactions.class));
                    }
                }
                adapter.refereshAdapter(transactions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
