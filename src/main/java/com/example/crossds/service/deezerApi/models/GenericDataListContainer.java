package com.example.crossds.service.deezerApi.models;

import java.util.List;

public class GenericDataListContainer<T> {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

