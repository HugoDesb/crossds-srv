package com.example.crossds.service;

import java.time.LocalDateTime;

public class Credentials {

    private String access_token;
    private String refresh_token;
    private LocalDateTime expirationDate;


    public Credentials(String access_token, String refresh_token, LocalDateTime expirationDate) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expirationDate = expirationDate;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
