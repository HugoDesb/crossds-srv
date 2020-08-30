package com.example.crossds.service.deezerApi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public class User {

    @JsonProperty("id")
    private String username;

    @JsonProperty("name")
    private String displayName;

    @JsonProperty("picture_medium")
    private URL picture_url;

    @JsonProperty("email")
    private String email;

    @JsonProperty("link")
    private URL link;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public URL getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(URL picture_url) {
        this.picture_url = picture_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }
}
