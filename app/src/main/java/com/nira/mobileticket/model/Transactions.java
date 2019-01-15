package com.nira.mobileticket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Transactions implements Parcelable {

    private String locomotive; // from where to where
    private String fare;
    private String bus_no;
    private String totalFare;
    private String validity;
    private String nTickets;
    private String status; //Failed or success


    public Transactions() {
    }


    public String getLocomotive() {
        return locomotive;
    }

    public void setLocomotive(String locomotive) {
        this.locomotive = locomotive;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getnTickets() {
        return nTickets;
    }

    public void setnTickets(String nTickets) {
        this.nTickets = nTickets;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBus_no() {
        return bus_no;
    }

    public void setBus_no(String bus_no) {
        this.bus_no = bus_no;
    }

    protected Transactions(Parcel in) {
        locomotive = in.readString();
        fare = in.readString();
        totalFare = in.readString();
        validity = in.readString();
        nTickets = in.readString();
        status = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(locomotive);
        dest.writeString(fare);
        dest.writeString(totalFare);
        dest.writeString(validity);
        dest.writeString(nTickets);
        dest.writeString(status);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transactions> CREATOR = new Parcelable.Creator<Transactions>() {
        @Override
        public Transactions createFromParcel(Parcel in) {
            return new Transactions(in);
        }

        @Override
        public Transactions[] newArray(int size) {
            return new Transactions[size];
        }
    };
}
