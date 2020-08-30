package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class GetTokensException extends GenericApiException {

    public GetTokensException(Platform platform) {
        super("Failed to get tokens", platform);
    }
}
