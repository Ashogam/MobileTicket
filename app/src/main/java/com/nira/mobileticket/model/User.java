package com.nira.mobileticket.model;

public class User {

    public String displayName;
    public String profilePicURL;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String displayName, String profilePicURL) {
        this.displayName = displayName;
        this.profilePicURL = profilePicURL;
    }
}
