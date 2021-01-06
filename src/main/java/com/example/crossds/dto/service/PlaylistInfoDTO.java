package com.example.crossds.dto.service;

import com.example.crossds.model.User;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.net.URI;

public class PlaylistInfoDTO {

    private Long id;
    private String snapshot_hash;
    private String platform_id;
    private URI link;
    private User owner;
}
