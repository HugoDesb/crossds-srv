package com.example.crossds.model;


import com.example.crossds.service.Credentials;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id_account;

    @Column(name = "display_name")
    private String displayName;

    private String username;

    @Column(columnDefinition = "varchar(2048)")
    private URL picture_url;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expiration_datetime", columnDefinition = "TIMESTAMP")
    private LocalDateTime expirationDateTime;

    @OneToMany(mappedBy = "account", cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch=FetchType.EAGER)
    private Set<AccountPlaylist> accountPlaylists;


    public Account() { }

    public Account(Account account) {
        this.id_account = account.id_account;
        this.displayName = account.displayName;
        this.username = account.username;
        this.picture_url = account.picture_url;
        this.email = account.email;
        this.platform = account.platform;
        this.refreshToken = account.refreshToken;
        this.accessToken = account.accessToken;
        this.expirationDateTime = account.expirationDateTime;
        this.accountPlaylists = account.accountPlaylists;
    }

    public Long getId_account() {
        return id_account;
    }

    public void setId_account(Long id_account) {
        this.id_account = id_account;
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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Set<AccountPlaylist> getAccountPlaylists() {
        return accountPlaylists;
    }

    public void setAccountPlaylists(Set<AccountPlaylist> accountPlaylists) {
        this.accountPlaylists = accountPlaylists;
    }

    public void addAccountPlaylist(AccountPlaylist accountPlaylist){ this.accountPlaylists.add(accountPlaylist);}

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(LocalDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public Credentials getCredentials(){
        return new Credentials(getAccessToken(), getRefreshToken(), getExpirationDateTime());
    }

    public void setCredentials(Credentials credentials){
        setExpirationDateTime(credentials.getExpirationDate());
        setRefreshToken(credentials.getRefresh_token());
        setAccessToken((credentials.getAccess_token()));
    }

    public boolean isSpotify(){
        return platform.equals(Platform.SPOTIFY);
    }

    public boolean isDeezer(){
        return platform.equals(Platform.DEEZER);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id_account +
                ", displayName='" + displayName + '\'' +
                ", username='" + username + '\'' +
                ", picture_url=" + picture_url +
                ", email='" + email + '\'' +
                ", platform='" + platform + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expirationDateTime=" + expirationDateTime +
                ", participations=" + accountPlaylists +
                '}';
    }
}
