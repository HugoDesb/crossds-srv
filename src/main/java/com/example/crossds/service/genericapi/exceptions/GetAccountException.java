package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class GetAccountException extends GenericApiException {

    public GetAccountException(Platform platform) {
        super("Cannot get the user", platform);
    }
}
