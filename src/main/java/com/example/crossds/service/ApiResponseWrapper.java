package com.example.crossds.service;

public class ApiResponseWrapper<T> {

    private Credentials credentials;
    private T data;

    public ApiResponseWrapper(Credentials credentials, T data) {
        this.credentials = credentials;
        this.data = data;
    }

    public ApiResponseWrapper() {
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public T getData() {
        return data;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setData(T data) {
        this.data = data;
    }
}
