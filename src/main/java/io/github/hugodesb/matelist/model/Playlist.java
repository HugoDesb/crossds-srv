package io.github.hugodesb.matelist.model;

import javax.persistence.*;
import java.net.URL;

@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String snapshot_hash;

    private String platform_id;

    private URL link;

    @OneToOne
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSnapshot_hash() {
        return snapshot_hash;
    }

    public void setSnapshot_hash(String snapshot_hash) {
        this.snapshot_hash = snapshot_hash;
    }

    public String getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(String platform_id) {
        this.platform_id = platform_id;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
