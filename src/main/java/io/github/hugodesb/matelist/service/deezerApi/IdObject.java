package io.github.hugodesb.matelist.service.deezerApi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdObject {

    @JsonProperty("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
