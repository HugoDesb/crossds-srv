package io.github.hugodesb.matelist.service.genericapi.exceptions;

import org.apache.hc.core5.http.HttpException;

public class GenericApiException extends HttpException {

    public GenericApiException() {
    }

    public GenericApiException(String message) {
        super(message);
    }

    public GenericApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
