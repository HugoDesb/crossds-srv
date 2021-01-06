package io.github.hugodesb.matelist.controller;

import io.github.hugodesb.matelist.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

}
