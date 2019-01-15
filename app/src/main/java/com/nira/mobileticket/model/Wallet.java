package com.nira.mobileticket.model;

public class Wallet {
    private int amount;
    private boolean locked;

    public Wallet() {

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
