package com.example.crossds.controller.reponse.exceptions;


import com.example.crossds.business.Platform;
import com.example.crossds.service.genericapi.exceptions.GenericApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Can't authenticate user")
public class AuthenticationException extends GenericApiException {

    public AuthenticationException(Platform platform) {
        super("Can't authenticate user", platform);
    }
}
