package io.github.hugodesb.matelist.service.genericapi;

import io.github.hugodesb.matelist.model.Account;
import io.github.hugodesb.matelist.model.Playlist;
import io.github.hugodesb.matelist.model.Track;
import io.github.hugodesb.matelist.model.Credentials;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
public interface GenericApiService {

    /**
     * Create the Oauth URI
     * @return the oauth uri
     */
    URI getOAuthUrl();

    /**
     * Gets the tokens corresponding to the temporary code
     * @param code The code
     * @return The credentials
     */
    Credentials getTokens(String code);


    /**
     * Gets the account info.
     * @param access_token a valid access_token
     * @return an account object
     */
    Account getCurrentAccount(String access_token);

    /**
     * Add tracks to the specified playlist.
     * @param access_token a valid access_token
     * @param playlistId The playlist id on the platform
     * @param tracksIds The list of tracks ids to add
     * @return the playlist's new hash
     */
    String addTracksToPlaylist(String access_token, String playlistId, List<String> tracksIds);

    /**
     * Gets the platform track id matching more closely this track.
     * @param access_token a valid access_token
     * @param track The track
     * @return the track id
     */
    String getServiceIdentifier(String access_token, Track track);

    /**
     * Gets the platform track id of the one matching the ISRC code
     * @param isrc The ISRC code
     * @return The track id
     */
    String getServiceIdentifierBasedOnIsrc(String isrc);

    /**
     * Gets the specified playlist tracks.
     * @param access_token the access_token
     * @param playlist_id The playlist id on the platform
     * @return a set of playlist tracks objects
     */
    Set<Track> getPlaylistTracks(String access_token, String playlist_id);

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
    String createPlaylist(String access_token, String userId, String title);

    /**
     * Gets a playlist snapshot hash
     * @param access_token a valid access_token
     * @param playlistId the playlist's id
     * @return the snapshot hash
     */
    String getPlaylistSnapshotHash(String access_token, String playlistId);


    Playlist getPlaylistInfo(String access_token, String playlistId);
}
