package io.github.hugodesb.matelist.service.deezerApi.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Album {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
