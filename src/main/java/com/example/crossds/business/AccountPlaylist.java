package com.example.crossds.business;

import javax.persistence.*;

@Entity
public class AccountPlaylist {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch=FetchType.EAGER)
    //@MapsId("id_account")
    @JoinColumn(name = "id_account")
    private Account account;

    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch=FetchType.EAGER)
    //@MapsId("id_playlist")
    @JoinColumn(name = "id_playlist")
    private CollaborativePlaylist collaborativePlaylist;

    private String service_playlist_id;

    private String snapshotHash;

    public AccountPlaylist() {
    }

    public AccountPlaylist(AccountPlaylist accountPlaylist) {
        this.id = accountPlaylist.id;
        this.account = accountPlaylist.account;
        this.collaborativePlaylist = accountPlaylist.collaborativePlaylist;
        this.service_playlist_id = accountPlaylist.service_playlist_id;
        this.snapshotHash = accountPlaylist.snapshotHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public CollaborativePlaylist getCollaborativePlaylist() {
        return collaborativePlaylist;
    }

    public void setCollaborativePlaylist(CollaborativePlaylist collaborativePlaylist) {
        collaborativePlaylist.addAccountPlaylist(this);
        this.collaborativePlaylist = collaborativePlaylist;

    }

    public String getService_playlist_id() {
        return service_playlist_id;
    }

    public void setService_playlist_id(String service_playlist_id) {
        this.service_playlist_id = service_playlist_id;
    }

    public String getSnapshotHash() {
        return snapshotHash;
    }

    public void setSnapshotHash(String snapshotHash) {
        this.snapshotHash = snapshotHash;
    }
}
