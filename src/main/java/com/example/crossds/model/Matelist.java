package com.example.crossds.model;


import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Matelist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(targetEntity = Playlist.class)
    private Set<Playlist> playlists;

    @ManyToMany(targetEntity = Track.class)
    private Set<Track> tracks;

    /**
     * GETTERS AND SETTERS
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public String toString() {
        return "Matelist{" +
                "id=" + id +
                ", playlists=" + playlists +
                ", tracks=" + tracks +
                '}';
    }

    public static class Builder {
        private Set<Playlist> playlists;
        private Set<Track> tracks;

        public Builder() {
            this.playlists = new HashSet<>();
            this.tracks = new HashSet<>();
        }

        public Builder addPlaylist(Playlist playlist){
            this.playlists.add(playlist);
            return this;
        }

        public Builder addPlaylists(Collection<Playlist> playlists){
            this.playlists.addAll(playlists);
            return this;
        }

        public Builder addTrack(Track track){
            this.tracks.add(track);
            return this;
        }

        public Builder addTracks(Collection<Track> tracks){
            this.tracks.addAll(tracks);
            return this;
        }

        public Matelist build(){
            Matelist matelist = new Matelist();
            matelist.setPlaylists(this.playlists);
            matelist.setTracks(this.tracks);
            return matelist;
        }
    }
}
