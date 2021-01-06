package io.github.hugodesb.matelist.repository;

import io.github.hugodesb.matelist.model.Playlist;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlaylistRepository extends CrudRepository<Playlist, Long> {

    public Optional<Playlist> findPlaylistByPlatform_id(String platform_id);

    public Optional<Playlist> findPlaylistByPlatform_idAAndOwnerId(String platform_id, Long owner_id);
}
