package io.github.hugodesb.matelist.controller;

import io.github.hugodesb.matelist.model.Account;
import io.github.hugodesb.matelist.model.Platform;
import io.github.hugodesb.matelist.service.DeezerApiService;
import io.github.hugodesb.matelist.service.SpotifyApiService;
import io.github.hugodesb.matelist.service.genericapi.GenericApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
public class MainController {

    private Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private SpotifyApiService spotifyApiService;

    @Autowired
    private DeezerApiService deezerApiService;

    @GetMapping("/hello/{name}")
    public @ResponseBody String sayHello(@PathVariable("name") String name){
        return "Hello, "+name;
    }

    public GenericApiService getApiService(Platform platform){
        switch (platform){
            case SPOTIFY:
                return spotifyApiService;
            case DEEZER:
                return deezerApiService;
        }
        throw new IllegalArgumentException("Can't get appropriate service for: "+ platform.getName());
    }

    public GenericApiService getApiServiceFor(Account account){
        switch (account.getPlatform()){
            case SPOTIFY:
                return spotifyApiService;
            case DEEZER:
                return deezerApiService;
        }
        throw new IllegalArgumentException("Can't get appropriate service for: "+ account.getPlatform());
    }


    public Platform getPlatform(String value){
        if(value.toLowerCase().trim().equals(Platform.SPOTIFY.getName())){
            return Platform.SPOTIFY;
        }

        if(value.toLowerCase().trim().equals(Platform.DEEZER.getName())){
            return Platform.DEEZER;
        }

        return null;
    }



}
