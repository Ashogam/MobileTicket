package com.nira.mobileticket.tickets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nira.mobileticket.R;
import com.nira.mobileticket.model.Transactions;

public class TransactionActivity extends AppCompatActivity {

    private TextView passenger_name, locomotive, n_tickets, validity, fare, total_fare;

    Transactions transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        passenger_name = findViewById(R.id.passenger_name);
        locomotive = findViewById(R.id.locomotive);
        n_tickets = findViewById(R.id.n_tickets);
        validity = findViewById(R.id.validity);
        fare = findViewById(R.id.fare);
        total_fare = findViewById(R.id.total_fare);

        if (getIntent() != null && getIntent().hasExtra("transaction")) {
            transactions = getIntent().getParcelableExtra("transaction");
        }



        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showData();
    }

    void showData(){
        if(transactions!=null) {
            locomotive.setText(transactions.getLocomotive());
            n_tickets.setText(transactions.getnTickets());
            validity.setText(transactions.getValidity());
            fare.setText(transactions.getFare());
            total_fare.setText(transactions.getTotalFare());
        }
    }


}
