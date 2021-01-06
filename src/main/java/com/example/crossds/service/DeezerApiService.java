package com.example.crossds.service;

import com.example.crossds.model.Account;
import com.example.crossds.model.Platform;
import com.example.crossds.model.Playlist;
import com.example.crossds.service.deezerApi.IdObject;
import com.example.crossds.service.deezerApi.User;
import com.example.crossds.service.deezerApi.models.PlaylistDzr;
import com.example.crossds.service.genericapi.GenericApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DeezerApiService implements GenericApiService {

    private static final String app_id = "407522";
    private static final String secret = "d482da32d0e5f1360bfa87e3ebb810a8";
    private static final String redirect_uri = "https://protected-tundra-83090.herokuapp.com/oauth/deezer/callback";
    private static final String permissions = "basic_access,manage_library,offline_access,email,delete_library";



    public URI getOAuthUrl() {
        String url = "https://connect.deezer.com/oauth/auth.php?app_id="+app_id+"&redirect_uri="+redirect_uri+"&perms="+permissions;
        return URI.create(url);
    }

    @Override
    public Credentials getTokens(String code) throws AuthenticationException{
        Credentials c ;
        try {
            WebClient webClient = WebClient.builder().baseUrl("https://connect.deezer.com").build();
            Mono<Token> mono = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/oauth/access_token.php")
                            .queryParam("app_id", app_id)
                            .queryParam("secret", secret)
                            .queryParam("code", code)
                            .queryParam("output", "json")
                            .build())
                    .retrieve()
                    .bodyToMono(Token.class);
            Token ret =  mono.block();
            c = new Credentials(ret.getAccessToken(), null, null);
        }catch (Exception e){
            e.printStackTrace();
            throw new AuthenticationException(Platform.DEEZER);
        }
        return c;
    }

    @Override
    public Account getCurrentAccount(String access_token) throws GetAccountException {

        Account account = new Account();
        User ret;
        try{
            WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
            Mono<User> mono = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/user/me")
                            .queryParam("access_token", access_token)
                            .build())
                    .retrieve()
                    .bodyToMono(User.class);
            ret =  mono.block();

            account.setDisplayName(ret.getDisplayName());
            account.setPicture_url(ret.getPicture_url());
            account.setEmail(ret.getEmail());
            account.setUsername(ret.getUsername());
            account.setPlatform(Platform.DEEZER);

            return account;
        }catch (Exception e){
            e.printStackTrace();
            throw new GetAccountException(Platform.DEEZER);
        }
    }

    @Override
    public List<Playlist> getAccountPlaylists(String access_token) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<PlaylistData> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user/me/playlists")
                        .queryParam("access_token", access_token)
                        .build())
                .retrieve()
                .bodyToMono(PlaylistData.class);
        PlaylistData ret =  mono.block();
        return ret.getData();
    }

    @Override
    public String createPlaylist(String access_token, String userId, String title) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<IdObject> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user/me/playlists")
                        .queryParam("access_token", access_token)
                        .queryParam("title", title)
                        .build())
                .retrieve()
                .bodyToMono(IdObject.class);
        IdObject ret =  mono.block();
        return ret.getId();
    }

    @Override
    public Playlist getPlaylistInfo(String access_token, String playlistId) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<PlaylistDzr> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/playlist/{playlist_id}")
                        .queryParam("access_token", access_token)
                        .build(playlistId))
                .retrieve()
                .bodyToMono(PlaylistDzr.class);
        PlaylistDzr ret =  mono.block();

        Playlist playlist = new Playlist();
        playlist.setPlatform_id(ret.getId().toString());
        playlist.setLink(ret.getLink());
        playlist.setSnapshot_hash(ret.getChecksum());

    }

    @Override
    public String addTracksToPlaylist(String access_token, String playlistId, List<String> tracksIds) {

        if(tracksIds.size()==0){
            throw new IllegalArgumentException("No tracks ids have been specified");
        }

        StringBuilder songs = new StringBuilder();
        for (String song :
                tracksIds) {
            songs.append(song).append(",");
        }
        songs.deleteCharAt(songs.length()-1);

        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<Boolean> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/playlist/{playlist_id}/tracks")
                        .queryParam("access_token", access_token)
                        .queryParam("songs", songs.toString())
                        .build(playlistId))
                .retrieve()
                .bodyToMono(Boolean.class);
        Boolean ret =  mono.block();

        if(ret){
            return playlistId;
        }else{
            throw new AddTracksException(Platform.DEEZER);
        }
    }

    @Override
    public String getServiceIdentifier(String access_token, com.example.crossds.model.Track t) {
        String firstAttempt = getServiceIdentifierBasedOnIsrc(t.getIsrc());
        if(firstAttempt!= null){
            return firstAttempt;
        }else{
            throw new GetTrackException(Platform.SPOTIFY);
        }
    }

    @Override
    public String getServiceIdentifierBasedOnIsrc(String isrc) {
        String isrc_string = "isrc:"+isrc;
        try{
            WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
            Mono<Track> mono = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/2.0/track/{isrc_string}")
                            .build(isrc_string))
                    .retrieve()
                    .bodyToMono(Track.class);
            Track ret =  mono.block();
            return ret.getId();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public String getTrackIsrc(String track_id){
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<Track> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/track/{track_id}")
                        .build(track_id))
                .retrieve()
                .bodyToMono(Track.class);
        Track ret =  mono.block();
        return ret.getIsrc();
    }

    @Override
    public Set<com.example.crossds.model.Track> getPlaylistTracks(Credentials credentials, String playlist_id) {
        Set<com.example.crossds.model.Track> tracks = new HashSet<>();
        com.example.crossds.model.Track trackToAdd;


        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<TracksData> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/playlist/{playlist_id}/tracks")
                        .queryParam("access_token", credentials.getAccess_token())
                        .build(playlist_id))
                .retrieve()
                .bodyToMono(TracksData.class);
        TracksData ret =  mono.block();

        for (Track t :
                ret.getData()) {
            trackToAdd = new com.example.crossds.model.Track();
            trackToAdd.setAlbum_name(t.getAlbum().getTitle());
            trackToAdd.setArtist_name(t.getArtist().getName());
            trackToAdd.setDeezer_identifier(t.getId());
            trackToAdd.setIsrc(getTrackIsrc(t.getId()));
            trackToAdd.setTrack_name(t.getTitle());
            tracks.add(trackToAdd);
        }

        return tracks;
    }

    @Override
    public String getPlayilstSnapshotHash(Credentials credentials, String playlistId) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<Playlist> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/playlist/{playlist_id}")
                        .queryParam("access_token", credentials.getAccess_token())
                        .build(playlistId))
                .retrieve()
                .bodyToMono(Playlist.class);
        Playlist ret =  mono.block();
        return ret.getChecksum();
    }

    @Override
    public Credentials refreshTokens(Credentials credentials) {
        return credentials;
    }
}
