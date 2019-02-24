package com.example.colecofer.android_audio_visualizer;

import android.util.Log;
import android.util.Pair;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
   * Parses timestamps and lyrics and stores them in a Arraylist of Millisecond/Word[] pairs
   *
   * <p>TODO: Delete fullString declaration and uncomment all commented lines once we have a
   * commercial apikey
   *
   * @param responseJSON
   * @return a ArrayList<Pair<Integer, String[]>> with milliseconds associated to a line of lyrics
   */
  public static ArrayList<Pair<Integer, String[]>> parseLyrics(String responseJSON) {
    ArrayList<Pair<Integer, String[]>> lyricList = new ArrayList<>();
    //        JSONObject json = SpotifyClient.convertStringToJSON(responseJSON);
    //        String fullString = "Error - Could not extract lyrics";
    String fullString =
        "[00:00.30] One, two, three!\n[00:01.53] My baby don't mess around\n[00:03.60] Because she loves me so\n[00:05.22] This I know fo sho!\n[00:09.74] But does she really wanna\n[00:12.14] But can't stand to see me walk out the door\n[00:18.04] Don't try to fight the feeling\n[00:20.61] Because the thought alone is killin' me right now\n[00:26.56] Thank God for Mom and Dad\n[00:28.78] For sticking to together\n[00:30.46] Like we don't know how\n[00:34.39] Hey ya! Hey ya!\n[00:41.57] Hey ya! Hey ya!\n[00:48.45] Hey ya! Hey ya!\n[00:55.38] Hey ya! Hey ya!\n[01:07.95] You think you've got it\n[01:09.03] Oh, you think you've got it\n[01:10.52] But got it just don't get it when there's nothin' at all\n[01:16.18] We get together\n[01:17.27] Oh, we get together\n[01:18.74] But separate's always better when there's feelings involved";

    //String fullString = biggieSmallsLyrics;
    //            try {
    //                fullString = json.getJSONObject("Lyrics").getString("subtitle_body");
    String lyricStrings[] =
        fullString
            .replaceFirst("\\[..:..\\...] ", "")
            .split(
                "\\[..:..\\...] "); // separates lyrics from [##:##.##] timestamps. Removes first
                                    // instance of delimiter to avoid leading empty string
    String timestampStrings[] = fullString.split("\\s.*(\\n|$)"); // extract [##:##.##] from lyrics
    if (lyricStrings.length != timestampStrings.length) {
      Log.d(
          "Spotify",
          "Error - Number of timestamps does not match with the number of extracted lines");
      return null;
    }
    for (int i = 0; i < lyricStrings.length; i++) {

      String segments[] =
          timestampStrings[i]
              .replaceFirst("\\D", "")
              .split("\\D"); // extract digits to calculate empty string
      int minutes = Integer.parseInt(segments[0]);
      int seconds = Integer.parseInt(segments[1]);
      int jiffies = Integer.parseInt(segments[2]);
      int durationInMillis = minutes * 60000 + seconds * 1000 + jiffies * 10;
      Pair currPair = new Pair(durationInMillis, lyricStrings[i].split(" "));

      lyricList.add(currPair);
      //                }
      //
      //            } catch (JSONException e) {
      //                Log.d("MusixMatch", "Error - Could not extract lyrics from response" +
      // e.getMessage());
      //                return null;
    }

    return lyricList;
    }

    private static String biggieSmallsLyrics = "[00:01.5]\n" +
            "Uhhh, uhhh, uh, c`mon\n" +
            "[00:06.5]\n" +
            "Hah, \n" +
            "sicka than your average \n" +
            "[00:08.0]\n" +
            "Poppa twist cabbage off instinct \n" +
            "[00:10.0]\n" +
            "Niggaz don`t think shit stink\n" +
            "pink gators,\n" +
            "[00:13:.0]\n" +
            "My Detroit players\n" +
            "[00:14.0]\n" +
            "Timbs for my hooligans in Brooklyn\n" +
            "[00:16.5]\n" +
            "Dead right, if they head right, \n" +
            "Biggie there ery’ night\n" +
            "[00:19.0]\n" +
            "Poppa been smooth \n" +
            "since days of Underroos\n" +
            "[00:21.75]\n" +
            "Never lose, \n" +
            "never choose to, \n" +
            "bruise crews who\n" +
            "[00:24.0]\n" +
            "Do something to us, \n" +
            "talk go through us\n" +
            "[00:27.0]\n" +
            "Girls walk to us, \n" +
            "wanna do us, \n" +
            "screw us\n" +
            "[00:29.75]\n" +
            "Who us? \n" +
            "Yeah, Poppa and Puff \n" +
            "[00:32.5]\n" +
            "Close like Starsky and Hutch, \n" +
            "stick the clutch\n" +
            "[00:34.6]\n" +
            "Dare I squeeze three \n" +
            "at your cherry M-3\n" +
            "[00:36.9]\n" +
            "Bang every MC \n" +
            "easily, busily\n" +
            "[00:39.7]\n" +
            "Recently niggaz frontin \n" +
            "ain`t sayin nuttin’\n" +
            "so I just \n" +
            "[00:42.6]\n" +
            "Speak my piece, \n" +
            "keep my peace\n" +
            "[00:45.25]\n" +
            "Cubans with the Jesus piece,\n" +
            "with my peeps\n" +
            "[00:47.6]\n" +
            "Packin, askin who want it, \n" +
            "you got it nigga flaunt it\n" +
            "[00:50.2]\n" +
            "That Brooklyn bullshit, \n" +
            "we on it\n" +
            "[01:13.0]\n" +
            "I put hoes in NY onto DKNY\n" +
            "[01:15.3]\n" +
            "Miami, D.C. prefer Versace\n" +
            "[01:18.5]\n" +
            "All Philly hoes, \n" +
            "dough and Moschino\n" +
            "[01:20.8]\n" +
            "Every cutie wit a booty bought a Coogi\n" +
            "[01:23.0]\n" +
            "Now who`s the real dookie, \n" +
            "meanin’ who`s really the shit\n" +
            "[01:25.5]\n" +
            "Them niggaz ride dicks, \n" +
            "Frank White push the sticks\n" +
            "[01:28.0]\n" +
            "On the Lexus, LX, \n" +
            "four and a half\n" +
            "[01:30.5]\n" +
            "Bulletproof glass \n" +
            "tints if I want some ass\n" +
            "[01:33.0]\n" +
            "Gon` blast squeeze first \n" +
            "ask questions last\n" +
            "[01:35.8]\n" +
            "That`s how most of these \n" +
            "so-called gangsters pass\n" +
            "[01:38.7]\n" +
            "At last, \n" +
            "a nigga rappin bout blunts and broads\n" +
            "[01:41.2]\n" +
            "Tits and bras, \n" +
            "menage-a-trois, \n" +
            "sex in expensive cars\n" +
            "[01:44.2]\n" +
            "I still leave you on the pavement\n" +
            "[01:46.3]\n" +
            "Condo paid for, \n" +
            "no car payment\n" +
            "[01:48.8]\n" +
            "At my arraignment, \n" +
            "note for the plantiff\n" +
            "[01:51.5]\n" +
            "Your daughter`s tied up \n" +
            "in a Brooklyn basement\n" +
            "[01:53.7]\n" +
            "Face it, not guilty, \n" +
            "that`s how I stay filthy\n" +
            "[01:56.8]\n" +
            "Richer than Richie,\n" +
            "till you niggaz come and get me\n" +
            "[02:19.7]\n" +
            "I can fill ya wit’ real millionaire shit\n" +
            "[02:22.2]\n" +
            "Escargot, my car go, \n" +
            "one sixty, swiftly\n" +
            "[02:25.5]\n" +
            "Wreck it buy a new one\n" +
            "[02:27.2]\n" +
            "Your crew run run run, \n" +
            "your crew run run\n" +
            "[02:30.0]\n" +
            "I know you sick of this, \n" +
            "name brand nigga wit’\n" +
            "[02:32.5]\n" +
            "Flows \n" +
            "girls say he`s sweet like licorice\n" +
            "[02:35.1]\n" +
            "So get with this nigga, \n" +
            "it`s easy\n" +
            "[02:37.2]\n" +
            "Girlfriend here`s a pen, \n" +
            "call me round ten\n" +
            "[02:40.0]\n" +
            "Come through, \n" +
            "have sex on rugs that`s Persian\n" +
            "[02:43.0]\n" +
            "Come up to your job, \n" +
            "hit you while you workin\n" +
            "for certain, \n" +
            "[02:45.3]\n" +
            "Poppa freakin,\n" +
            "not speakin’\n" +
            "[02:47.7]\n" +
            "Leave that ass leakin, \n" +
            "like rapper demo\n" +
            "[02:50.0]\n" +
            "Tell them hoe, \n" +
            "take they clothes off slowly\n" +
            "[02:52.7]\n" +
            "Hit `em wit the force like Obe, \n" +
            "dick black like Toby\n" +
            "[02:55.0]\n" +
            "Watch me roam like Gobe,\n" +
            "lucky they don`t owe me\n" +
            "[02:57.8]\n" +
            "Where the safe? \n" +
            "Show me, homie!\n";

}