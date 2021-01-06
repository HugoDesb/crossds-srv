package io.github.hugodesb.matelist.model;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_track;

    private String isrc;

    private String track_name;

    private String album_name;

    private String artist_name;

    private boolean spotify_availability;

    private String spotify_identifier;

    private boolean deezer_availability;

    private String deezer_identifier;

    @ManyToMany(mappedBy = "tracks", cascade={CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<CollaborativePlaylist> collaborativePlaylists;

    public Track() { }

    public Track(Track track) {
        this.deezer_identifier = track.deezer_identifier;
        this.deezer_availability = track.deezer_availability;
        this.spotify_identifier = track.spotify_identifier;
        this.spotify_availability = track.spotify_availability;
        this.album_name = track.album_name;
        this.artist_name = track.artist_name;
        this.collaborativePlaylists = track.collaborativePlaylists;
        this.id_track = track.id_track;
        this.track_name = track.track_name;
        this.isrc = track.isrc;
    }

    public Long getId_track() {
        return id_track;
    }

    public void setId_track(Long id_track) {
        this.id_track = id_track;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getTrack_name() {
        return track_name;
    }

    public void setTrack_name(String track_name) {
        this.track_name = track_name;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getSpotify_identifier() {
        return spotify_identifier;
    }

    public void setSpotify_identifier(String spotify_identifier) {
        this.spotify_identifier = spotify_identifier;
    }

    public String getDeezer_identifier() {
        return deezer_identifier;
    }

    public void setDeezer_identifier(String deezer_identifier) {
        this.deezer_identifier = deezer_identifier;
    }

    public Set<CollaborativePlaylist> getCollaborativePlaylists() {
        return collaborativePlaylists;
    }

    public void setCollaborativePlaylists(Set<CollaborativePlaylist> collaborativePlaylists) {
        this.collaborativePlaylists = collaborativePlaylists;
    }

    public boolean isSpotify_availability() {
        return spotify_availability;
    }

    public void setSpotify_availability(boolean spotify_availability) {
        this.spotify_availability = spotify_availability;
    }

    public boolean isDeezer_availability() {
        return deezer_availability;
    }

    public void setDeezer_availability(boolean deezer_availability) {
        this.deezer_availability = deezer_availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(isrc, track.isrc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isrc);
    }

    public boolean hasServiceIdentifier(Platform platform){
        switch (platform){
            case SPOTIFY:
                return spotify_identifier!=null && !spotify_identifier.isEmpty();
            case DEEZER:
                return deezer_identifier!= null && !deezer_identifier.isEmpty();
        }
        return false;
    }

    public static class Builder {
        private Long id_track;
        private String isrc;
        private boolean spotify_availability;
        private String spotify_identifier;
        private boolean deezer_availability;
        private String deezer_identifier;

        public Track buildFromSpotifyTrack(com.wrapper.spotify.model_objects.specification.Track other){
            Track t = new Track();
            t.setIsrc(other.getExternalIds().getExternalIds().get("isrc"));
            t.setSpotify_identifier(other.getId());
            t.setSpotify_availability(true);
            return t;
        }

    }
}
