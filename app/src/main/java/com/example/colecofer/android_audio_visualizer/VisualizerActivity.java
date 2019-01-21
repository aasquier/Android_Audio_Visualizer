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

        surfaceView = new VisualizerSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            log.d("opengl", "Supports ES2");
            // Request an OpenGL ES 2.0 compatible context.
            surfaceView.setEGLContextClientVersion(2);

            visualizerRenderer = new VisualizerRenderer(audioSampleSize);
            // Set the renderer to our demo renderer, defined below.
            surfaceView.setRenderer(visualizerRenderer, displayMetrics.density, audioSampleSize);
        } else {
            log.d("opengl", "Does not support ES2");
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        //Sets up the visualizer for local files
        mediaPlayer = MediaPlayer.create(this, R.raw.jumparound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(audioSampleSize);
        visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        visualizer.setEnabled(true);

        setContentView(surfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }
        log.d("test", "TrackID: " + VisualizerModel.getInstance().getTrackURI());

        //TODO: Update the playbackState...

    }
}
