package io.github.hugodesb.matelist.dto.service;

import io.github.hugodesb.matelist.model.User;

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
