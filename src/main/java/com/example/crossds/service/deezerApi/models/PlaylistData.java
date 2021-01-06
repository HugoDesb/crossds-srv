package com.example.crossds.service.deezerApi.models;

import java.util.List;

public class PlaylistData {

    private List<PlaylistDzr> data;

    public List<PlaylistDzr> getData() {
        return data;
    }

    public void setData(List<PlaylistDzr> data) {
        this.data = data;
    }
}
