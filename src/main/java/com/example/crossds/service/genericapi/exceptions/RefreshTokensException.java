package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class RefreshTokensException extends GenericApiException {
    public RefreshTokensException(Platform platform) {
        super("Can't refresh Tokens", platform);
    }
}
