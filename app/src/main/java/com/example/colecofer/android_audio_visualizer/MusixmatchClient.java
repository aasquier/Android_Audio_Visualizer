package com.example.colecofer.android_audio_visualizer;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;

public class MusixmatchClient {

    private static final String SEARCH_URL = "http://api.musixmatch.com/ws/1.1/matcher.track.get";
    private static final String MATCHER_URL = "https://api.musixmatch.com/ws/1.1/track.subtitle.get";

    /**
     * Queries for MusixMatch track info based on the trackName and artist pulled from Spotify. Primary interest is track ID
     * @param trackName
     * @param artist
     * @param callback
     */
    public static void trackSearch(String trackName, String artist, final MusixmatchRequestCallBack callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("format", "json");
        params.put("callback", "callback");
        params.put("q_track", trackName);
        params.put("q_artist", artist);
        params.put("apikey", "5ad66be966fed184e1e2a939de699f22");

        client.get(SEARCH_URL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.musixmatchResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.musixmatchResponse(false, responseString);
            }

        });
    }

    /**
     * Extracts track ID from a JSON reponse
     * @param responseJSON
     * @return
     */
    public static int getTrackId(String responseJSON) {
        JSONObject json = SpotifyClient.convertStringToJSON(responseJSON);
        int value = -1;
        try {
            value = json.getJSONObject("message").getJSONObject("body").getJSONObject("track").getInt("track_id");
        } catch (JSONException e) {
            Log.d("MusixMatch", "Error - Could not extract track ID from response" + e.getMessage());
        }
        return value;
    }

    /**
     * Uses track ID to query musixmatch for a JSON response that includes lyrics + timestamps
     *
     * TODO: Acquire commercial apikey. GET request returns 403 error without it
     * @param responseJSON
     * @param callback
     */
    public static void getLyrics(String responseJSON, final MusixmatchRequestCallBack callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        int trackId = getTrackId((responseJSON));
        RequestParams params = new RequestParams();
        params.put("format", "json");
        params.put("callback", "callback");
        params.put("commontrack_id", trackId);
        params.put("apikey", "5ad66be966fed184e1e2a939de699f22");

        //Uncomment the following and edit the second argument to specify how often we would like a new timestamp
        //params.put("f_subtitle_length", 0);
        //Uncomment the following and edit the second argument to specify how much the client can deviate from the requested subtitle length
        //params.put("f_subtitle_length_max_deviation", 0);


        client.get(MATCHER_URL, params, new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                callback.musixmatchResponse(true, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callback.musixmatchResponse(false, responseString);
            }

        });
    }

    /**
     * Parses timestamps and lyrics and stores them in a treemap as Key/Value pairs
     *
     * TODO: Delete fullString declaration and uncomment all commented lines once we have a commercial apikey
     *
     * @param responseJSON
     * @return a TreeMap<Integer, String[]> that uses milliseconds as a key for an array of strings containing 1 word each
     */
    public static TreeMap<Integer, String[]> parseLyrics(String responseJSON) {
        TreeMap valueMap = new TreeMap();
//        JSONObject json = SpotifyClient.convertStringToJSON(responseJSON);
//        String fullString = "Error - Could not extract lyrics";
        String fullString = "[00:00.30] One, two, three!\n[00:01.53] My baby don't mess around\n[00:03.60] Because she loves me so\n[00:05.22] This I know fo sho!\n[00:09.74] But does she really wanna\n[00:12.14] But can't stand to see me walk out the door\n[00:18.04] Don't try to fight the feeling\n[00:20.61] Because the thought alone is killin' me right now\n[00:26.56] Thank God for Mom and Dad\n[00:28.78] For sticking to together\n[00:30.46] Like we don't know how\n[00:34.39] Hey ya! Hey ya!\n[00:41.57] Hey ya! Hey ya!\n[00:48.45] Hey ya! Hey ya!\n[00:55.38] Hey ya! Hey ya!\n[01:07.95] You think you've got it\n[01:09.03] Oh, you think you've got it\n[01:10.52] But got it just don't get it when there's nothin' at all\n[01:16.18] We get together\n[01:17.27] Oh, we get together\n[01:18.74] But separate's always better when there's feelings involved";
//            try {
//                fullString = json.getJSONObject("Lyrics").getString("subtitle_body");
                String lyricStrings[] = fullString.replaceFirst("\\[..:..\\...] ", "").split("\\[..:..\\...] "); //separates lyrics from [##:##.##] timestamps. Removes first instance of delimiter to avoid leading empty string
                String timestampStrings[] = fullString.split("\\s.*(\\n|$)");  //extract [##:##.##] from lyrics
                if (lyricStrings.length != timestampStrings.length) {
                    Log.d("Spotify", "Error - Number of timestamps does not match with the number of extracted lines");
                    return null;
                }
                for (int i = 0; i < lyricStrings.length; i++) {

                    String segments[] = timestampStrings[i].replaceFirst("\\D", "").split("\\D"); //extract digits to calculate empty string
                    int minutes = Integer.parseInt(segments[0]);
                    int seconds = Integer.parseInt(segments[1]);
                    int jiffies = Integer.parseInt(segments[2]);
                    int durationInMillis = minutes * 60000 + seconds * 1000 + jiffies * 10;

                    valueMap.put(durationInMillis, lyricStrings[i].split(" "));
//                }
//
//            } catch (JSONException e) {
//                Log.d("MusixMatch", "Error - Could not extract lyrics from response" + e.getMessage());
//                return null;
            }

                return valueMap;
        }


}