package com.example.indoorairqualitymonitoring.model;

import com.google.gson.annotations.SerializedName;

public class Token
{
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private int expire;

    public Token(String accessToken, int expire) {
        this.accessToken = accessToken;
        this.expire = expire;
    }

    public String getAccessToken()
    {
        return this.accessToken;
    }
    public int getExpire(){ return this.expire; }
}
