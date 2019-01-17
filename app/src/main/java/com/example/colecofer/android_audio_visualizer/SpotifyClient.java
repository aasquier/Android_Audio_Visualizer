package com.example.colecofer.android_audio_visualizer;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.os.StrictMode;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import android.support.v7.graphics.Palette;
import android.graphics.Bitmap;
import android.media.Image;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import static com.loopj.android.http.AsyncHttpClient.log;

public class SpotifyClient {

    private final String BASE_URL = "https://api.spotify.com/v1";
    private final String FEATURES_URL = "/audio-features/";
    private final String SEARCH_URL = "/search/";
    private final String PLAYER_URL = "/me/player/";
    private final String TRACK_URL = "/tracks/";
    private final String ARTIST_URL = "/artists/";
    private final String ALBUM_URL = "";
    private final String COVER_ART_URL = "https://getsongbpm.com/cache/img/album/3cdaf15ab6101706f4320a6349149ed9.jpg";
    private final String AUTH_URL = "https://accounts.spotify.com/api/token";

    //TODO: Change these to our personal information (this is a public repo...)
    private final String CLIENT_ID = "5f0eac9db12042cfa8b9fb95b0f3f4d8";
    private final String CLIENT_SECRET = "4f0d128f8f1b4776a530292cdef1dd45";

    private final static String SPOTIFY_TAG = "SPOTIFY";


    /**
     * Gets the authorization token given the clientid and clientsecret
     * @param scopes
     * @param callback
     */
    public void getAuthToken(String scopes, final SpotifyRequestCallBack callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams body = new RequestParams();
        body.put("client_id", CLIENT_ID);
        body.put("client_secret", CLIENT_SECRET);
        body.put("grant_type", "client_credentials");
        body.put("scope", scopes);

        client.post(AUTH_URL, body, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }

    /**
     * Recieves general information about a track given a track ID.
     * Specifically
     * @param trackID
     * @param authToken
     * @param callback
     */
    public void getTrackInfo(String trackID, String authToken, final SpotifyRequestCallBack callback) {
        String fullFeaturesURL = BASE_URL + TRACK_URL + trackID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + authToken);

        client.get(fullFeaturesURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }

    /**
     * Gets the artist name given the Spotify ID
     * @param trackID The Spotify / Track ID
     * @param authToken The authentication token
     * @param callback Callback function to be implemented
     */
    public void getArtistName(String trackID, String authToken, final SpotifyRequestCallBack callback) {
        String fullArtistURL = BASE_URL + ARTIST_URL + trackID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authToken);

        client.get(fullArtistURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }

    /**
     * Gets the album name given a Spotify albumID
     * @param albumID
     * @param authToken
     * @param callback
     */
    public void getAlbumName(String albumID, String authToken, final SpotifyRequestCallBack callback) {
        String fullArtistURL = BASE_URL + ALBUM_URL + albumID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authToken);

        client.get(fullArtistURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }
    public void getAlbumArt(String albumID, String authToken, final SpotifyRequestCallBack callback) {
        String fullArtistURL = BASE_URL + ALBUM_URL + albumID;
        //String URL = this.COVER_ART_URL;


        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", authToken);

        client.get(fullArtistURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

        });
    }

    /**
     * Calls the Spotify features API end-point for the passed in trackID.
     * TODO: Remove hard coded auth token
     * @param trackID The ID for any given Spotify song
     * @param callback function that gets invoked after success or failure
     */
    public void getFeaturesFromTrackID(String trackID, String authToken, final SpotifyRequestCallBack callback) {
        String fullFeaturesURL = BASE_URL + FEATURES_URL + trackID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + authToken);

        client.get(fullFeaturesURL, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }


            @Override
            public void onStart() {
                Log.d("HTTP", "Request is starting...");
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("HTTP", "Request is retrying...");
            }

        });
    }


    /**
     * Calls the Spotify search API end-point and returns the JSON response of search
     * queries through the callback function.
     * TODO: Remove hard coded auth token
     * @param query Name of the song
     * @param searchType A list of types to search for (e.g. "album, artist, playlist, track")
     * @param callback function that gets invoked after success or failure
     */
    public void searchSpotify(String query, String searchType, String authToken, final SpotifyRequestCallBack callback) {
        String fullSearchURL = BASE_URL + SEARCH_URL;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
        client.addHeader("Authorization", "Bearer " + authToken);

        RequestParams params = new RequestParams();
        params.put("q", query);
        params.put("type", searchType);

        client.get(fullSearchURL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("HTTP", "Status Code: " + statusCode);
                callback.spotifyResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.spotifyResponse(false, responseString);
            }

            @Override
            public void onStart() {
                Log.d("HTTP", "Request is starting...");
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("HTTP", "Request is retrying...");
            }
        });
    }


    /**
     * Converts a String in JSON format to a JSONObject
     * @param jsonStr The string in JSON format
     * @return The JSONObject, or null if there was an error
     */
    public static JSONObject convertStringToJSON(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            return jsonObj;
        } catch (JSONException e) {
            Log.d(SPOTIFY_TAG, "Could not convert into JSONObject" + e.getMessage());
            return null;
        }
    }


    /**
     * Parses a json block and returns the passed in field
     * @param responseJSON The json block from the Spotify request
     * @return The desired field
     */
    public static String parseFieldFromJSON(String responseJSON, String field) {
        JSONObject json = convertStringToJSON(responseJSON);
        String value = "Error: Couldn't find field";
        try {
            value = json.getString(field);
        } catch (JSONException e) {
            Log.d("Spotify", "Error - Could not extract field from response" + e.getMessage());
        }
        return value;
    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            try {
                connection.connect();
            }
            catch (Exception e) {
                Log.d("getBitmapFromURL", "Error - Could not connect " + e.getMessage());
            }
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
