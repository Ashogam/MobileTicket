package com.nira.mobileticket.tickets;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nira.mobileticket.R;
import com.nira.mobileticket.model.Transactions;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private List<Transactions> transactions;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, passenger_count, date, fare_amount, status, validity, bus;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            bus = view.findViewById(R.id.bus);
            passenger_count = (TextView) view.findViewById(R.id.passenger_count);
            date = (TextView) view.findViewById(R.id.date);
            fare_amount = (TextView) view.findViewById(R.id.fare_amount);
            status = (TextView) view.findViewById(R.id.status);
            validity = view.findViewById(R.id.validity);
        }
    }


    public TransactionAdapter(List<Transactions> moviesList) {
        this.transactions = moviesList;
    }


    public void refereshAdapter(List<Transactions> data) {
        this.transactions = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_list, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Transactions movie = transactions.get(position);
        holder.title.setText(movie.getLocomotive());
        holder.passenger_count.setText(movie.getnTickets());
        holder.date.setText(convertTime(movie.getValidity()));
        holder.fare_amount.setText("Total Fare " + movie.getTotalFare());
        holder.bus.setText(movie.getBus_no());
        if (calculateDifference(Long.parseLong(movie.getValidity()))) {
            holder.validity.setText("Valid Ticket");
            holder.validity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.validity.setText("Invalid Ticket");
            holder.validity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

    }


    boolean calculateDifference(long sourceTime) {
        long time = new Date().getTime() - sourceTime;
        long minutes = time / (60 * 1000);
        if (minutes <= 15) {
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public String convertTime(String time) {
        Date date = new Date(Long.valueOf(time));
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }
}
