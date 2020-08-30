package com.example.crossds.service.genericapi;

import com.example.crossds.business.Account;
import com.example.crossds.business.Track;
import com.example.crossds.service.ApiResponseWrapper;
import com.example.crossds.service.Credentials;
import com.example.crossds.service.deezerApi.models.Playlist;

import java.net.URI;
import java.util.List;
import java.util.Set;

public interface GenericApiService {

    /**
     * Gets the Oauth URI
     * @return
     */
    URI getOAuthUrl();

    /**
     * Gets the tokens corresponding to the temporary code
     * @param code The code
     * @return The credentials
     */
    Credentials getTokens(String code);

    /**
     * Gets the account linked to the credentials.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @return An ApiResponseWrapper object containing the account as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<Account> getCurrentAccount(Credentials credentials);

    /**
     * Add tracks to the specified playlist.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @param playlistId The playlist id on the platform
     * @param tracksIds The list of tracks ids to add
     * @return An ApiResponseWrapper object containing the playlist new hash as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<String> addTracksToPlaylist(Credentials credentials, String playlistId, List<String> tracksIds);

    /**
     * Gets the platform track id matching more closely this track.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @param track The track
     * @return An ApiResponseWrapper object containing the track id as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<String> getServiceIdentifier(Credentials credentials, Track track);

    /**
     * Gets the list of user's playlist.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @return An ApiResponseWrapper object containing a List playlists as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<List<Playlist>> getAccountPlaylists(Credentials credentials);

    /**
     * Gets the platform track id of the one matching the ISRC code
     * @param isrc The ISRC code
     * @return The track id
     */
    String getServiceIdentifierBasedOnIsrc(String isrc);

    /**
     * Gets the specified playlist tracks.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @param playlist_id The playlist id on the platform
     * @return An ApiResponseWrapper object containing a set of the playlist tracks as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<Set<Track>> getPlaylistTracks(Credentials credentials, String playlist_id);

    /**
     * Refresh and returns the tokens
     * @param credentials The credentials
     * @return The newly refreshed tokens as credentials
     */
    Credentials refreshTokens(Credentials credentials);

    /**
     * Creates the playlist on the platform. If the operation is successfull, returns the playlist id of the platform as the data property
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @param userId The user id on the platform
     * @param title The title of the playlist
     * @return An ApiResponseWrapper object containing the playlist id as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<String> createPlaylist(Credentials credentials, String userId, String title);

    /**
     * Gets the playlist hash on the platform. If the operation is successfull, returns the playlist hash as the data property.
     * If needed, refresh the tokens.
     * @param credentials The credentials
     * @param playlistId The playlist id on the platform
     * @return An ApiResponseWrapper object containing the playlist new hash as the data property, and the newly refreshed tokens as credentials
     */
    ApiResponseWrapper<String> getPlayilstSnapshotHash(Credentials credentials, String playlistId);


}
