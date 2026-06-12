package com.sukesh.sentinelAuth.dto;

public class LogoutRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LogoutRequest()
    {

    }

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
