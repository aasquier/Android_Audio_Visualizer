package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;

import com.spotify.sdk.android.player.SpotifyPlayer;

public class Visualizer_Model {

    SpotifyPlayer player;
    String trackName;
    String artistName;
    String albumName;
    int primary, secondary, tertiary;


    /**
     * Default Constructor
     */
    public Visualizer_Model() {
    trackName = "Not defined";
    artistName = "Not defined";
    albumName = "Not defined";
    primary = Color.rgb(53, 86, 81);
    secondary = Color.rgb(0, 0 ,0);
    tertiary = Color.rgb(0, 0, 0);
    }

}
