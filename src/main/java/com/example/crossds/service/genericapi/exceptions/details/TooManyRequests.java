package com.example.crossds.service.genericapi.exceptions.details;

import com.example.crossds.service.genericapi.GenericApiService;
import com.example.crossds.service.genericapi.exceptions.GenericApiException;

public class TooManyRequests extends GenericApiException {
    public TooManyRequests() {
    }

    public TooManyRequests(String message) {
        super(message);
    }

    public TooManyRequests(String message, Throwable cause) {
        super(message, cause);
    }
}
