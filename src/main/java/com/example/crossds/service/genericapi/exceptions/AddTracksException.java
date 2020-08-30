package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class AddTracksException extends GenericApiException {
    public AddTracksException(Platform platform) {
        super("Can't add tracks to playlist", platform);
    }
}
