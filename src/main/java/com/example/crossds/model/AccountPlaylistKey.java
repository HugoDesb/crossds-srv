package com.example.crossds.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountPlaylistKey implements Serializable {

    @Column(name = "id_account")
    private Long accountId;

    @Column(name="id_playlist")
    private Long playlistId;

    public AccountPlaylistKey(Long accountId, Long playlistId) {
        this.accountId = accountId;
        this.playlistId = playlistId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountPlaylistKey that = (AccountPlaylistKey) o;
        return Objects.equals(accountId, that.accountId) &&
                Objects.equals(playlistId, that.playlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, playlistId);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }
}
