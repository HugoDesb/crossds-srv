package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class PlaylistNotFound extends GenericApiException{

    public PlaylistNotFound(Platform platform) {
        super("Can't find the playlist", platform);
    }
}
