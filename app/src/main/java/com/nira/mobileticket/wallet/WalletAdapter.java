package com.nira.mobileticket.wallet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nira.mobileticket.R;
import com.nira.mobileticket.Utils;
import com.nira.mobileticket.model.WalletTransactions;

import java.util.ArrayList;
import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.MyViewHolder> {

    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amount, date, status;

        public MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            amount = view.findViewById(R.id.amount);
            status = view.findViewById(R.id.status);
        }
    }

    List<WalletTransactions> transactions = new ArrayList<>();

    WalletAdapter() {

    }


    void setDataAdapter(List<WalletTransactions> transactions){
        this.transactions = transactions;
    }


    void setDataAdapter(){
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_wallet_list, viewGroup, false);
        context = viewGroup.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        WalletTransactions movie = transactions.get(i);
        holder.amount.setText(movie.getAmount());
        holder.status.setText(movie.getStatus());
        holder.date.setText(Utils.convertTime(movie.getCreatedDate()) + " " + movie.getTxnID());
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

}
