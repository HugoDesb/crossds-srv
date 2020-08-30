package com.example.crossds.service;

import com.example.crossds.business.Account;
import com.example.crossds.business.Platform;
import com.example.crossds.controller.reponse.exceptions.AuthenticationException;
import com.example.crossds.service.deezerApi.IdObject;
import com.example.crossds.service.deezerApi.User;
import com.example.crossds.service.deezerApi.models.*;
import com.example.crossds.service.genericapi.GenericApiService;
import com.example.crossds.service.genericapi.exceptions.AddTracksException;
import com.example.crossds.service.genericapi.exceptions.GetAccountException;
import com.example.crossds.service.genericapi.exceptions.GetTrackException;
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
    private static final String redirect_uri = "http://localhost:8080/api/oauth/deezer/callback";
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
    public ApiResponseWrapper<Account> getCurrentAccount(Credentials credentials) throws GetAccountException {

        Account account = new Account();
        User ret;
        try{
            WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
            Mono<User> mono = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/user/me")
                            .queryParam("access_token", credentials.getAccess_token())
                            .build())
                    .retrieve()
                    .bodyToMono(User.class);
            ret =  mono.block();


            account.setCredentials(credentials);
            account.setDisplayName(ret.getDisplayName());
            account.setPicture_url(ret.getPicture_url());
            account.setEmail(ret.getEmail());
            account.setUsername(ret.getUsername());
            account.setPlatform(Platform.DEEZER);

            return new ApiResponseWrapper<>(credentials, account);
        }catch (Exception e){
            e.printStackTrace();
            throw new GetAccountException(Platform.DEEZER);
        }
    }

    @Override
    public ApiResponseWrapper<List<Playlist>> getAccountPlaylists(Credentials credentials) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<PlaylistData> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user/me/playlists")
                        .queryParam("access_token", credentials.getAccess_token())
                        .build())
                .retrieve()
                .bodyToMono(PlaylistData.class);
        PlaylistData ret =  mono.block();
        return new ApiResponseWrapper<>(credentials, ret.getData());
    }

    @Override
    public ApiResponseWrapper<String> createPlaylist(Credentials credentials, String userId, String title) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<IdObject> mono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user/me/playlists")
                        .queryParam("access_token", credentials.getAccess_token())
                        .queryParam("title", title)
                        .build())
                .retrieve()
                .bodyToMono(IdObject.class);
        IdObject ret =  mono.block();
        return new ApiResponseWrapper<>(credentials, ret.getId());
    }

    @Override
    public ApiResponseWrapper<String> addTracksToPlaylist(Credentials credentials, String playlistId, List<String> tracksIds) {

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
                        .queryParam("access_token", credentials.getAccess_token())
                        .queryParam("songs", songs.toString())
                        .build(playlistId))
                .retrieve()
                .bodyToMono(Boolean.class);
        Boolean ret =  mono.block();

        if(ret){
            return getPlayilstSnapshotHash(credentials, playlistId);
        }else{
            throw new AddTracksException(Platform.DEEZER);
        }
    }

    @Override
    public ApiResponseWrapper<String> getServiceIdentifier(Credentials credentials, com.example.crossds.business.Track t) {
        String firstAttempt = getServiceIdentifierBasedOnIsrc(t.getIsrc());
        if(firstAttempt!= null){
            return new ApiResponseWrapper<String>(credentials, firstAttempt);
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
    public ApiResponseWrapper<Set<com.example.crossds.business.Track>> getPlaylistTracks(Credentials credentials, String playlist_id) {
        Set<com.example.crossds.business.Track> tracks = new HashSet<>();
        com.example.crossds.business.Track trackToAdd;


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
            trackToAdd = new com.example.crossds.business.Track();
            trackToAdd.setAlbum_name(t.getAlbum().getTitle());
            trackToAdd.setArtist_name(t.getArtist().getName());
            trackToAdd.setDeezer_identifier(t.getId());
            trackToAdd.setIsrc(getTrackIsrc(t.getId()));
            trackToAdd.setTrack_name(t.getTitle());
            tracks.add(trackToAdd);
        }

        return new ApiResponseWrapper<>(credentials, tracks);
    }

    @Override
    public ApiResponseWrapper<String> getPlayilstSnapshotHash(Credentials credentials, String playlistId) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.deezer.com").build();
        Mono<Playlist> mono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/playlist/{playlist_id}")
                        .queryParam("access_token", credentials.getAccess_token())
                        .build(playlistId))
                .retrieve()
                .bodyToMono(Playlist.class);
        Playlist ret =  mono.block();
        return new ApiResponseWrapper<>(credentials, ret.getChecksum());
    }

    @Override
    public Credentials refreshTokens(Credentials credentials) {
        return credentials;
    }
}
