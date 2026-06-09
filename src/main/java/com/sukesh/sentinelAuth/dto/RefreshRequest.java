package com.sukesh.sentinelAuth.dto;

public class RefreshRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshRequest()
    {

    }
    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
