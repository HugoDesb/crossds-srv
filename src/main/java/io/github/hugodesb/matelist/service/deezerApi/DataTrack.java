package io.github.hugodesb.matelist.service.deezerApi;



import io.github.hugodesb.matelist.service.deezerApi.models.Track;

import java.util.List;

public class DataTrack {

    private List<Track> data;

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }
}
