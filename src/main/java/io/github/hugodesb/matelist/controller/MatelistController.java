package io.github.hugodesb.matelist.controller;

import io.github.hugodesb.matelist.dto.input.CreateMatelistDTO;
import io.github.hugodesb.matelist.model.Matelist;
import io.github.hugodesb.matelist.model.Playlist;
import io.github.hugodesb.matelist.model.Track;
import io.github.hugodesb.matelist.model.User;
import io.github.hugodesb.matelist.repository.MatelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/matelist")
@Controller
public class MatelistController {

    @Autowired
    private MatelistRepository matelistRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private PlaylistController playlistController;

    @Autowired
    private TrackController trackController;

    /**
     * Gets the Matelist by its id
     * @param id the matelist identifier
     * @return a Matelist Object
     * @throws NoSuchElementException if the matelist is not found
     */
    @GetMapping("/{id}/")
    public Matelist getMatelist(@PathVariable("id") Long id) throws NoSuchElementException{
        Optional<Matelist> matelist = matelistRepository.findById(id);

        if(!matelist.isPresent()){
            throw new NoSuchElementException();
        }
        return matelist.get();
    }

    @DeleteMapping("/{id}/")
    public void deleteMatelist(@PathVariable("id") Long id){
        Matelist matelist = getMatelist(id);
        matelistRepository.delete(matelist);
    }

    @GetMapping("/{id}/users")
    public List<User> getMatelistUsers(@PathVariable("id") Long id){
        return getMatelist(id).getPlaylists().stream().map(Playlist::getOwner).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}/users/{uid}")
    public void removeMatelistUser(@PathVariable("id") Long id, @PathVariable("uid") Long uid){
        Matelist matelist = getMatelist(id);
        //this line remo
        matelist.setPlaylists(
                matelist.getPlaylists()
                        .stream()
                        .filter(playlist -> !playlist.getOwner().getId().equals(uid))
                        .collect(Collectors.toSet()));
        matelistRepository.save(matelist);
    }

    @PostMapping
    public @ResponseBody Matelist createMatelist(@RequestParam(name = "name") String name, @RequestParam("playlists") List<CreateMatelistDTO> playlists){
        Matelist.Builder matelistBuilder = new Matelist.Builder();

        playlists.forEach( npl -> {
            // TODO : see if need to be removed
            User user = userController.getUser(npl.getUser_id());
            Playlist playlist;

            // Gérer la creation d'une playlist, ou le lien avec une existante
            if(npl.isCreateNew()){
                // Creer la playlist distante
                playlist = playlistController.createPlaylist(name, user.getId());
            }else{
                // Obtenir la playlist existante
                playlist = playlistController.getPlaylistInfo(user, npl.getPlatformPlaylistId());
            }
            matelistBuilder.addPlaylist(playlist);
        });

        Matelist matelist = matelistBuilder.build();
        matelistRepository.save(matelist);
        // TODO: maybe use a DTO here
        return matelist;
    }


    @GetMapping("/{id}/tracks")
    public Set<Track> getMatelistTracks(@PathVariable("id") Long id){
        return getMatelist(id).getTracks();
    }

    // TODO: implement
    @PutMapping("/{id}/")
    public void updateMatelist(@PathVariable("id") Long id){
        //lets break it down into small pieces
        //aim to limit the number of API calls to either platforms rather than limit data (cause data isn't costly that much)

        //I want to check before hand for each user if their playlist was changed.
        //      If not -> no need to get all tracks
        //      else   -> get all tracks
        //By now we have all tracks that are new (union of all)
        //Consolidate all tracks obj

        // If no playlist was changed -> No need to update at all

        // If at least one was changed
        //      i need all tracks from all playlists to compute the difference between the new songs and the local songs -> then add the result
        //      When new songs are found
        //          are they in db ? if not add them with identifiers from all platforms

        Matelist matelist = getMatelist(id);
        if(needsUpdate(matelist)){
            // get tracks in matelist
            Set<Playlist> playlists = matelist.getPlaylists();
            Set<Track> tracks = matelist.getTracks();

            // init the map with at least the tracks in db
            Map<Playlist, Set<Track>> ref = playlists.stream().collect(Collectors.toMap(playlist -> playlist, o -> tracks ));

            for (Playlist pl : ref.keySet()) {
                ref.put(pl, playlistController.fetchTracks(pl.getOwner(), pl.getPlatform_id()));
            }



            // get each tracklist from each playlist
            //      expand it by adding all tracks not in db but remote (in, not in partition might be useful)
            //      for those present remotely but not in db -> consolidate and add to db
            // into a map of type <Playlist, List<Track>>
            // At that point we have all tracks from all playlist in the map.
            // Now, what we want to
        }
    }

    /**
     * Returns whether a matelist should be updated
     * true if at least one of the playlist was modified (snapshot hash is different)
     * false otherwise
     * @param matelist
     * @return
     */
    private boolean needsUpdate(Matelist matelist){
        return matelist.getPlaylists()
                .stream()
                .map(playlistController::hasChanged)
                .reduce((aBoolean, aBoolean2) -> aBoolean || aBoolean2)
                .orElse(false);
    }


}
