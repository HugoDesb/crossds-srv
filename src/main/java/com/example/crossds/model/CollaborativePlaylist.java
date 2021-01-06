package com.example.crossds.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class CollaborativePlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_playlist;

    private String name;

    @OneToMany(mappedBy = "collaborativePlaylist", cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch=FetchType.EAGER)
    private Set<AccountPlaylist> accountPlaylists;

    @ManyToMany(cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "collaborativeplaylist_track",
        joinColumns = @JoinColumn(name = "id_playlist"),
        inverseJoinColumns = @JoinColumn(name = "id_track"))
    private Set<Track> tracks;


    public CollaborativePlaylist(){
        tracks = new HashSet<>();
        accountPlaylists = new HashSet<>();
    }

    public Long getId_playlist() {
        return id_playlist;
    }

    public void setId_playlist(Long id_playlist) {
        this.id_playlist = id_playlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AccountPlaylist> getAccountPlaylists() {
        return accountPlaylists;
    }

    public void setAccountPlaylists(Set<AccountPlaylist> accountPlaylists) {
        this.accountPlaylists = accountPlaylists;
    }

    public void addAccountPlaylist(AccountPlaylist accountPlaylist){
        this.accountPlaylists.add(accountPlaylist);
    }
    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }
}
