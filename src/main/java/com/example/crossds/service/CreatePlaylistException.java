package com.example.crossds.service;

import com.example.crossds.business.Platform;
import com.example.crossds.service.genericapi.exceptions.GenericApiException;

public class CreatePlaylistException extends GenericApiException {
    public CreatePlaylistException(Platform platform) {
        super("Can't create playlist", platform);
    }
}
