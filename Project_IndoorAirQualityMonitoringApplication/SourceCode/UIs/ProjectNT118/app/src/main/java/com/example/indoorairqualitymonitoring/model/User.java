package com.example.indoorairqualitymonitoring.model;

import com.google.gson.annotations.SerializedName;

public class User
{
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("createdOn")
    private long createdOn;
    @SerializedName("username")
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}