package io.github.hugodesb.matelist.service.genericapi.exceptions.details;

import io.github.hugodesb.matelist.service.genericapi.exceptions.GenericApiException;

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
