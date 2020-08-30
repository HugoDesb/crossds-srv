package com.example.crossds.service;

import com.example.crossds.business.Account;
import com.example.crossds.business.Platform;
import com.example.crossds.business.Track;
import com.example.crossds.controller.reponse.exceptions.AuthenticationException;
import com.example.crossds.repository.AccountRepository;
import com.example.crossds.service.genericapi.GenericApiService;
import com.example.crossds.service.genericapi.exceptions.*;
import com.google.gson.JsonArray;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.User;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;
import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import org.apache.hc.core5.http.ParseException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SpotifyApiService implements GenericApiService {

    private static final String clientId = "8f6c8d03f6804e17b0757a1645854d4f";
    private static final String clientSecret = "b1567c32522c434c8c25611c51ac9d8f";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/api/oauth/spotify/callback");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    private String clientCredentialsAccessToken;
    private LocalDateTime expirationDate;

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private AccountRepository accountRepository;

    /**
     * Generates the OAuth URI
     * @return OAuth uri
     */
    public URI getOAuthUrl() {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .state("x4xkmn9pu3j6ukrs8n")
                .scope("user-read-private,user-read-email,playlist-read-collaborative,playlist-read-private,playlist-modify-public,playlist-modify-private")
                .show_dialog(true)
                .build();
        URI uri = authorizationCodeUriRequest.execute();
        return uri;
    }


    /**
     * Given a unique code, retreives the credentials
     * @param code unique code provided by spotify
     * @return
     */
    @Override
    public Credentials getTokens(String code){
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code)
                .build();
        try {
            AuthorizationCodeCredentials a = authorizationCodeRequest.execute();

            LocalDateTime expirationDate = LocalDateTime.now().plus(Long.parseLong(a.getExpiresIn().toString()), ChronoUnit.SECONDS);
            return new Credentials(a.getAccessToken(), a.getRefreshToken(), expirationDate );

        } catch (IOException | ParseException e) {
            e.printStackTrace();

        } catch (SpotifyWebApiException e){
            throw new AuthenticationException(Platform.SPOTIFY);
        }
        return null;
    }

    public void refreshClientCredentialsTokensIfNecessary(){
        if(expirationDate == null || expirationDate.isBefore(LocalDateTime.now())){
            updateClientCredentialsFlowTokens();
        }
    }

    public void updateClientCredentialsFlowTokens(){
        ClientCredentialsRequest request = spotifyApi.clientCredentials().grant_type("client_credentials").build();
        try{
            ClientCredentials clientCredentials = request.execute();
            this.clientCredentialsAccessToken = clientCredentials.getAccessToken();
            this.expirationDate = LocalDateTime.now().plus(Long.parseLong(clientCredentials.getExpiresIn().toString()), ChronoUnit.SECONDS);
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            throw new RefreshTokensException(Platform.SPOTIFY);
        }
    }


    /**
     * Given valid credentials, obtains the current user
     * @param credentials
     * @return
     */
    public ApiResponseWrapper<Account> getCurrentAccount(Credentials credentials){
        Account account = new Account();
        //refresh if necessary
        Credentials newCredentials = refreshTokensIfNecessary(credentials);

        spotifyApi.setAccessToken(newCredentials.getAccess_token());
        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                .build();
        try {
            final User user = getCurrentUsersProfileRequest.execute();

            account.setPlatform(Platform.SPOTIFY);
            account.setUsername(user.getId());
            account.setDisplayName(user.getDisplayName());
            account.setPicture_url(new URL(user.getImages()[0].getUrl()));
            account.setEmail(user.getEmail());
            account.setCredentials(newCredentials);

            return new ApiResponseWrapper<>(newCredentials, account);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new GetAccountException(Platform.SPOTIFY);
        }
    }

    /**
     * Update the tokens if necessary
     * @param credentials
     */
    public Credentials refreshTokensIfNecessary(Credentials credentials){

        if(credentials.getExpirationDate().isBefore(LocalDateTime.now())){
            return refreshTokens(credentials);
        }else{
            return credentials;
        }
    }

    public boolean areCredentialsObsolete(Credentials credentials){
        return credentials.getExpirationDate().isBefore(LocalDateTime.now());
    }


    @Override
    public ApiResponseWrapper<String> addTracksToPlaylist(Credentials credentials, String playlistId, List<String> tracksIds) {
        Credentials newCredentials = refreshTokensIfNecessary(credentials);

        spotifyApi.setAccessToken(newCredentials.getAccess_token());
        if(tracksIds.isEmpty()){
            throw new AddTracksException(Platform.SPOTIFY);
        }

        int offset=0;
        int maxIndex;
        String snapshotId  = null;
        do{
            JsonArray trackIdsJsonArray = new JsonArray();
            maxIndex = Math.min(100, tracksIds.size()-offset);
            tracksIds.subList(offset, maxIndex).forEach(track->{
                trackIdsJsonArray.add(track);
            });

            AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi.addItemsToPlaylist(playlistId, trackIdsJsonArray).build();

            try{
                SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
                snapshotId = snapshotResult.getSnapshotId();

            } catch (ParseException | SpotifyWebApiException | IOException e) {
                e.printStackTrace();
                throw new AddTracksException(Platform.SPOTIFY);
            }
            offset+=100;
        }while(offset<=tracksIds.size());

        return new ApiResponseWrapper<>(newCredentials, snapshotId);
    }

    @Override
    public ApiResponseWrapper<String> getServiceIdentifier(Credentials credentials, Track t) {
        String firstAttempt = getServiceIdentifierBasedOnIsrc(t.getIsrc());
        if(firstAttempt!= null){
            return new ApiResponseWrapper<String>(credentials, firstAttempt);
        }else{
            throw new GetTrackException(Platform.SPOTIFY);
        }
    }

    @Override
    public ApiResponseWrapper<List<com.example.crossds.service.deezerApi.models.Playlist>> getAccountPlaylists(Credentials credentials) {
        //TODO : implement function
        throw new NotYetImplementedException();
    }

    @Override
    public String getServiceIdentifierBasedOnIsrc(String isrc) throws GetTrackException{
        refreshClientCredentialsTokensIfNecessary();
        spotifyApi.setAccessToken(clientCredentialsAccessToken);
        final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks("isrc:"+isrc).build();

        try{
            final Paging<com.wrapper.spotify.model_objects.specification.Track> tracks = searchTracksRequest.execute();
            if(tracks.getTotal() == 0){
                return null;
            }else{
                return tracks.getItems()[0].getUri();
            }
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            throw new GetTrackException(Platform.SPOTIFY);
        }
    }

    @Override
    public ApiResponseWrapper<Set<Track>> getPlaylistTracks(Credentials credentials, String playlistId){
        Credentials newCredentials = refreshTokensIfNecessary(credentials);

        Track trackToAdd;
        Set<Track> out = new HashSet<>();
        int offset = 0;
        GetPlaylistsItemsRequest getPlaylistsItemsRequest;
        Paging<PlaylistTrack> pagedTracks;
        PlaylistTrack [] test;

        spotifyApi.setAccessToken(newCredentials.getAccess_token());
        do{
            getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).offset(offset).build();
            try{
                pagedTracks = getPlaylistsItemsRequest.execute();
                test = pagedTracks.getItems();
                for (PlaylistTrack t :
                        test) {
                    if(t.getTrack().getType()== ModelObjectType.TRACK){
                        com.wrapper.spotify.model_objects.specification.Track track = (com.wrapper.spotify.model_objects.specification.Track) t.getTrack();
                        trackToAdd = new Track();
                        trackToAdd.setTrack_name(track.getName());
                        trackToAdd.setIsrc(track.getExternalIds().getExternalIds().get("isrc"));
                        trackToAdd.setArtist_name(track.getArtists()[0].getName());
                        trackToAdd.setAlbum_name(track.getAlbum().getName());
                        trackToAdd.setSpotify_identifier(track.getId());
                        out.add(trackToAdd);
                    }
                }

                if(pagedTracks.getNext()!=null){
                    offset += test.length;
                }else{
                    offset = 0;
                }
            } catch (ParseException | SpotifyWebApiException | IOException e) {
                e.printStackTrace();
            }
        }while(offset!=0);

        return new ApiResponseWrapper<>(newCredentials, out);
    }

    /**
     * Refresh the credentials (refresh access_tokens)
     * @param credentials
     * @return
     */
    @Override
    public Credentials refreshTokens(Credentials credentials){
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().refresh_token(credentials.getRefresh_token()).build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            LocalDateTime expirationDate = LocalDateTime.now().plus(Long.parseLong(authorizationCodeCredentials.getExpiresIn().toString()), ChronoUnit.SECONDS);
            return new Credentials(authorizationCodeCredentials.getAccessToken(), credentials.getRefresh_token(), expirationDate );
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RefreshTokensException(Platform.SPOTIFY);
        }
    }

    @Override
    public ApiResponseWrapper<String> createPlaylist(Credentials credentials, String userId, String title){
        Credentials newCredentials = refreshTokensIfNecessary(credentials);

        spotifyApi.setAccessToken(newCredentials.getAccess_token());
        final CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, title).build();
        try{
            final Playlist playlist = createPlaylistRequest.execute();
            return new ApiResponseWrapper<>(newCredentials, playlist.getId());
        }catch (IOException | ParseException | SpotifyWebApiException e){
            throw new CreatePlaylistException(Platform.SPOTIFY);
        }
    }

    @Override
    public ApiResponseWrapper<String> getPlayilstSnapshotHash(Credentials credentials, String playlistId) {
        Credentials newCredentials = refreshTokensIfNecessary(credentials);

        spotifyApi.setAccessToken(newCredentials.getAccess_token());
        final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();

        try{
            final Playlist playlist = getPlaylistRequest.execute();

            return new ApiResponseWrapper<>(newCredentials, playlist.getSnapshotId());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            throw new PlaylistNotFound(Platform.SPOTIFY);
        }
    }
}