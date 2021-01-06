package com.example.crossds.service;

import com.example.crossds.model.Account;
import com.example.crossds.model.Platform;
import com.example.crossds.model.Playlist;
import com.example.crossds.model.Track;
import com.example.crossds.service.genericapi.GenericApiService;
import com.google.gson.JsonArray;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.Paging;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpotifyApiService implements GenericApiService {

    private static final String clientId = "8f6c8d03f6804e17b0757a1645854d4f";
    private static final String clientSecret = "b1567c32522c434c8c25611c51ac9d8f";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("https://protected-tundra-83090.herokuapp.com/oauth/spotify/callback");

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build();

    private String clientCredentialsAccessToken;
    private LocalDateTime expirationDate;

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

    private void refreshClientCredentialsTokensIfNecessary(){
        if(expirationDate == null || expirationDate.isBefore(LocalDateTime.now())){
            updateClientCredentialsFlowTokens();
        }
    }

    private void updateClientCredentialsFlowTokens(){
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
     * Given valid access_token, obtains the current user
     * @param access_token
     * @return
     */
    public Account getCurrentAccount(String access_token){
        Account account = new Account();

        spotifyApi.setAccessToken(access_token);
        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile()
                .build();
        try {
            final User user = getCurrentUsersProfileRequest.execute();

            account.setPlatform(Platform.SPOTIFY);
            account.setUsername(user.getId());
            account.setDisplayName(user.getDisplayName());
            account.setPicture_url(new URL(user.getImages()[0].getUrl()));
            account.setEmail(user.getEmail());

            return account;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new GetAccountException(Platform.SPOTIFY);
        }
    }

    @Override
    public String addTracksToPlaylist(String access_token, String playlistId, List<String> tracksIds) {
        spotifyApi.setAccessToken(access_token);
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

        return snapshotId;
    }

    @Override
    public String getServiceIdentifier(String access_token, Track t) {
        String firstAttempt = getServiceIdentifierBasedOnIsrc(t.getIsrc());
        if(firstAttempt!= null){
            return firstAttempt;
        }else{
            throw new GetTrackException(Platform.SPOTIFY);
        }
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
    public Set<Track> getPlaylistTracks(String access_token, String playlistId){

        Set<Track> out = new HashSet<>();
        int offset = 0;
        GetPlaylistsItemsRequest getPlaylistsItemsRequest;
        Paging<PlaylistTrack> pagedTracks;
        PlaylistTrack [] items;

        spotifyApi.setAccessToken(access_token);

        try{
            getPlaylistsItemsRequest = spotifyApi.getPlaylistsItems(playlistId).offset(offset).build();
            do{
                pagedTracks = getPlaylistsItemsRequest.execute();
                out.addAll(Arrays.stream(pagedTracks.getItems())
                        .filter(playlistTrack -> playlistTrack.getTrack().getType() == ModelObjectType.TRACK)
                        .map(playlistTrack -> new Track.Builder().buildFromSpotifyTrack((com.wrapper.spotify.model_objects.specification.Track) playlistTrack.getTrack()))
                        .collect(Collectors.toList()));

                offset = (!pagedTracks.getNext().isEmpty()) ? offset+100 : 0;
            }while(offset!=0);

        }catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }


        return out;
    }

    /**
     * Refresh the credentials (refresh access_tokens)
     * @param credentials the credentials
     * @return the updated credentials
     */
    @Override
    public Credentials refreshTokens(Credentials credentials){
        final AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().refresh_token(credentials.getRefresh_token()).build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            LocalDateTime expirationDate = LocalDateTime.now().plus(Long.parseLong(authorizationCodeCredentials.getExpiresIn().toString()), ChronoUnit.SECONDS);
            credentials.setExpirationDate(expirationDate);
            credentials.setAccess_token(authorizationCodeCredentials.getAccessToken());
            credentials.setRefresh_token(authorizationCodeCredentials.getRefreshToken());

            return credentials;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RefreshTokensException(Platform.SPOTIFY);
        }
    }

    @Override
    public String createPlaylist(String access_token, String userId, String title){
        spotifyApi.setAccessToken(access_token);
        final CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, title).build();
        try{
            final com.wrapper.spotify.model_objects.specification.Playlist playlist = createPlaylistRequest.execute();
            return playlist.getId();
        }catch (IOException | ParseException | SpotifyWebApiException e){
            throw new CreatePlaylistException(Platform.SPOTIFY);
        }
    }

    @Override
    public String getPlaylistSnapshotHash(String access_token, String playlistId){
        spotifyApi.setAccessToken(access_token);
        final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
        try{
            final com.wrapper.spotify.model_objects.specification.Playlist playlist = getPlaylistRequest.execute();
            return playlist.getSnapshotId();
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            throw new PlaylistNotFound(Platform.SPOTIFY);
        }
    }

    @Override
    public Playlist getPlaylistInfo(String access_token, String playlistId) {
        spotifyApi.setAccessToken(access_token);
        final GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
        try{
            final com.wrapper.spotify.model_objects.specification.Playlist playlist = getPlaylistRequest.execute();
            Playlist playlist1 = new Playlist();
            playlist1.setPlatform_id(playlist.getId());
            playlist1.setLink(URI.create(playlist.getExternalUrls().get("spotify")));
            playlist1.setSnapshot_hash(playlist.getSnapshotId());
            return playlist1;
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            throw new PlaylistNotFound(Platform.SPOTIFY);
        }
    }
}
