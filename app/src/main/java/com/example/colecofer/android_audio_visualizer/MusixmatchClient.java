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

import static com.example.colecofer.android_audio_visualizer.Constants.DEMO_MODE;

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
     * TODO: Delete fullString declaration and uncomment all commented lines once we have a commercial apikey
     *
     * @param responseJSON
     * @return a ArrayList<Pair<Integer, String[]>> with milliseconds associated to a line of lyrics
     */
    public static ArrayList<Pair<Integer, String[]>> parseLyrics(String responseJSON) {
        ArrayList<Pair<Integer, String[]>> lyricList= new ArrayList<>();

        //JSONObject json = SpotifyClient.convertStringToJSON(responseJSON);
        //String fullString = "Error - Could not extract lyrics";

        //Demo lyrics
        String fullString = "";
        String HEY_YA_LYRICS = "[00:00.30] One, two, three!\n[00:01.53] My baby don't mess around\n[00:03.60] Because she loves me so\n[00:05.22] This I know fo sho!\n[00:09.74] But does she really wanna\n[00:12.14] But can't stand to see me walk out the door\n[00:18.04] Don't try to fight the feeling\n[00:20.61] Because the thought alone is killin' me right now\n[00:26.56] Thank God for Mom and Dad\n[00:28.78] For sticking to together\n[00:30.46] Like we don't know how\n[00:34.39] Hey ya! Hey ya!\n[00:41.57] Hey ya! Hey ya!\n[00:48.45] Hey ya! Hey ya!\n[00:55.38] Hey ya! Hey ya!\n[01:07.95] You think you've got it\n[01:09.03] Oh, you think you've got it\n[01:10.52] But got it just don't get it when there's nothin' at all\n[01:16.18] We get together\n[01:17.27] Oh, we get together\n[01:18.74] But separate's always better when there's feelings involved";

        if (DEMO_MODE == true) {
            fullString = BIGGIE_LYRICS;
        } else {
            fullString = HEY_YA_LYRICS;
        }

        //try {
        //fullString = json.getJSONObject("Lyrics").getString("subtitle_body");

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
            Pair currPair = new Pair(durationInMillis, lyricStrings[i].split(" "));

            lyricList.add(currPair);
        }
        // } catch (JSONException e) {
        //Log.d("MusixMatch", "Error - Could not extract lyrics from response" + e.getMessage());
        //return null;
        //}

        Log.d("test", "lyricList: " + lyricList);
        return lyricList;
    }
    //}

    //These are strictly for demo purposes only - since we don't have a musixmatch license.
    private static String BIGGIE_LYRICS =
            "[00:01.50] Uhhh, uhhh, uh, c'mon\n" +
                    "[00:06.50] Hah,\t" +
                    "sicka than your average\n" +
                    "[00:08.00] Poppa twist cabbage off instinct\n" +
                    "[00:10.00] Ninjaz don't think skunks stink\t" +
                    "pink gators,\n" +
                    "[00:13.00] My Detroit players\n" +
                    "[00:14.00] Timbs for my hooligans in Brooklyn\n" +
                    "[00:16.50] Dead right, if they head right,\t" +
                    "Biggie there ery’ night\n" +
                    "[00:19.00] Poppa been smooth\t" +
                    "since days of Underroos\n" +
                    "[00:21.75] Never lose,\t" +
                    "never choose to,\t" +
                    "bruise crews who\n" +
                    "[00:24.00] Do something to us,\t" +
                    "talk go through us\n" +
                    "[00:27.00] Girls walk to us,\t" +
                    "wanna help us,\t" +
                    "shop wit us\n" +
                    "[00:29.75] Who us?\t" +
                    "Yeah, Poppa and Puff\n" +
                    "[00:32.50] Close like Starsky and Hutch,\t" +
                    "stick the clutch\n" +
                    "[00:34.60] Dare I squeeze three\t" +
                    "at your cherry M-3\n" +
                    "[00:36.90] Bang every MC\t" +
                    "easily, busily\n" +
                    "[00:39.70] Recently ninjaz frontin\t" +
                    "ain't sayin nuttin’\t" +
                    "so I just\n" +
                    "[00:42.60] Speak my piece,\t" +
                    "keep my peace\n" +
                    "[00:45.25] Cubans with the Jesus piece,\t" +
                    "with my peeps\n" +
                    "[00:47.60] Packin, askin who want it,\t" +
                    "you got it ninja flaunt it\n" +
                    "[00:50.20] That Brooklyn bullpucky,\t" +
                    "we on it\n" +
                    "[00:54.00] \n" +
                    "[01:13.00] I put hopes in NY onto DKNY\n" +
                    "[01:15.30] Miami, D.C. prefer Versace\n" +
                    "[01:18.50] All Philly hopes,\t" +
                    "dough and Moschino\n" +
                    "[01:20.80] Every cutie wit a degree bought a Coogi\n" +
                    "[01:23.00] Now who's the real dookie,\t" +
                    "meanin’ who's really the best\n" +
                    "[01:25.50] Them ninjaz ride horses,\t" +
                    "Frank White push the sticks\n" +
                    "[01:28.00] On the Lexus, LX,\t" +
                    "four and a half\n" +
                    "[01:30.50] Bulletproof glass\t" +
                    "tints if I want some class\n" +
                    "[01:33.00] Gon' blast squeeze first\t" +
                    "ask questions last\n" +
                    "[01:35.80] That's how most of these\t" +
                    "so-called gangsters pass\n" +
                    "[01:38.70] At last,\t" +
                    "a ninja rappin bout blunts and broads\n" +
                    "[01:41.20] Mountains and lakes,\t" +
                    "french words,\t" +
                    "ridin' in expensive cars\n" +
                    "[01:44.20] I still leave you on the pavement\n" +
                    "[01:46.30] Condo paid for,\t" +
                    "no car payment\n" +
                    "[01:48.80] At my arraignment,\t" +
                    "note for the plantiff\n" +
                    "[01:51.50] Your daughter's art studio is\t" +
                    "in a Brooklyn basement\n" +
                    "[01:53.70] Face it, not guilty,\t" +
                    "that's how I stay filthy\n" +
                    "[01:56.80] Richer than Richie,\t" +
                    "till you ninjaz come and get me\n" +
                    "[01:60.80] \n" +
                    "[02:19.70] I can fill ya wit’ real millionaire stuff\n" +
                    "[02:22.20] Escargot, my car go,\t" +
                    "one sixty, swiftly\n" +
                    "[02:25.50] Wreck it buy a new one\n" +
                    "[02:27.20] Your crew run run run,\t" +
                    "your crew run run\n" +
                    "[02:30.00] I know you sick of this,\t" +
                    "name brand ninja wit’\n" +
                    "[02:32.50] Flows\t" +
                    "girls say he's sweet like licorice\n" +
                    "[02:35.10] So get with this ninja,\t" +
                    "it's easy\n" +
                    "[02:37.20] Girlfriend here's a pen,\t" +
                    "call me round ten\n" +
                    "[02:40.00] Come through,\t" +
                    "have great conversation on rugs that's Persian\n" +
                    "[02:43.00] Come up to your job,\t" +
                    "hit you while you workin\t" +
                    "for certain,\n" +
                    "[02:45.30] Poppa freakin,\t" +
                    "not speakin’\n" +
                    "[02:47.70] Leave that beverage leakin',\t" +
                    "like rapper demo\n" +
                    "[02:50.00] Tell them ladies,\t" +
                    "take they stickers off slowly\n" +
                    "[02:52.70] Hit 'em wit the force like Obe,\t" +
                    "car black like Toby's\n" +
                    "[02:55.00] Watch me roam like Gobe,\t" +
                    "lucky they don't owe me\n" +
                    "[02:57.80] Where the safe?\t" +
                    "Show me, homie!";

}