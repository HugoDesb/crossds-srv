package com.example.crossds.controller.reponse;

import com.example.crossds.business.Account;

public class AccountResponse {

    private String id;
    private String displayName;
    private String username;
    private String pictureUrl;
    private String platform;

    public AccountResponse(Account account){
        this.id = Long.toString(account.getId_account());
        this.displayName = account.getDisplayName();
        this.username = account.getUsername();
        this.pictureUrl = account.getPicture_url().toString();
        this.platform = account.getPlatform().getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
