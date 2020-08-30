package com.example.crossds.service.genericapi.exceptions;

import com.example.crossds.business.Platform;

public class GetTrackException extends GenericApiException {
    public GetTrackException(Platform platform) {
        super("Can't find track", platform);
    }
}
