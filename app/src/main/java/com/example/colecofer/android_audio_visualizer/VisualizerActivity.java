package com.example.colecofer.android_audio_visualizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.loopj.android.http.AsyncHttpClient.log;

public class VisualizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);

        SpotifyPlayer player = VisualizerModel.getInstance().getPlayer();
        player.playUri(MainActivity.operationCallback, VisualizerModel.getInstance().getTrackURI(), 0, 0);

        log.d("test", "TrackID: " + VisualizerModel.getInstance().getTrackURI());

        //TODO: Update the playbackState...

    }
}
