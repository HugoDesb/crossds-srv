package com.example.crossds.controller;

import com.example.crossds.model.Track;
import com.example.crossds.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TrackController {

    @Autowired
    private TrackRepository trackRepository;

}
