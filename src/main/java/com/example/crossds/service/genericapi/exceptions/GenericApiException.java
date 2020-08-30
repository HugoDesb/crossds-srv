package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class GenericApiException extends RuntimeException{

    private String message;

    public GenericApiException(String message, Platform platform) {
        this.message = message + " on platform["+platform.getName()+"]";
    }
}
