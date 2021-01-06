package io.github.hugodesb.matelist.service.deezerApi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDzr {

    private Long id;	// The playlist's Deezer id	int
    private String title; //	The playlist's title	string
    private String description; //	The playlist description	string
    private int duration; //	The playlist's duration (seconds)	int
    private boolean isPublic; //	If the playlist is public or not	boolean
    private boolean is_loved_track; //	If the playlist is the love tracks playlist	boolean
    private boolean collaborative; //	If the playlist is collaborative or not	boolean
    private int rating; //	The playlist's rate	int
    private int nb_tracks; //	Nb tracks in the playlist	int
    private int unseen_track_count; //	Nb tracks not seen	int
    private int fans; //	The number of playlist's fans	int
    private URL link; //	The url of the playlist on Deezer	url
    private URL share; //	The share link of the playlist on Deezer	url
    private URL picture; //	The url of the playlist's cover. Add 'size' parameter to the url to change size. Can be 'small', 'medium', 'big', 'xl'	url
    private URL picture_small; //	The url of the playlist's cover in size small.	url
    private URL picture_medium; //	The url of the playlist's cover in size medium.	url
    private URL picture_big; //	The url of the playlist's cover in size big.	url
    private URL picture_xl; //The url of the playlist's cover in size xl.	url
    private String checksum; //The checksum for the track list	string


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isIs_loved_track() {
        return is_loved_track;
    }

    public void setIs_loved_track(boolean is_loved_track) {
        this.is_loved_track = is_loved_track;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNb_tracks() {
        return nb_tracks;
    }

    public void setNb_tracks(int nb_tracks) {
        this.nb_tracks = nb_tracks;
    }

    public int getUnseen_track_count() {
        return unseen_track_count;
    }

    public void setUnseen_track_count(int unseen_track_count) {
        this.unseen_track_count = unseen_track_count;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public URL getShare() {
        return share;
    }

    public void setShare(URL share) {
        this.share = share;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }

    public URL getPicture_small() {
        return picture_small;
    }

    public void setPicture_small(URL picture_small) {
        this.picture_small = picture_small;
    }

    public URL getPicture_medium() {
        return picture_medium;
    }

    public void setPicture_medium(URL picture_medium) {
        this.picture_medium = picture_medium;
    }

    public URL getPicture_big() {
        return picture_big;
    }

    public void setPicture_big(URL picture_big) {
        this.picture_big = picture_big;
    }

    public URL getPicture_xl() {
        return picture_xl;
    }

    public void setPicture_xl(URL picture_xl) {
        this.picture_xl = picture_xl;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
