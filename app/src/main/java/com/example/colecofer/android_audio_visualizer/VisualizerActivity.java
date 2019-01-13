package com.example.colecofer.android_audio_visualizer;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.loopj.android.http.AsyncHttpClient.log;

public class VisualizerActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private static final int REQUEST_PERMISSION = 101;

//    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private VisualizerSurfaceView surfaceView;
    private VisualizerRenderer visualizerRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);

        startTrackPlayback();
        setupVisualizer();
    }

    /**
     * Invokes track playback using the SpotifyPlayer in VisualizerModel
     */
    private void startTrackPlayback() {
        SpotifyPlayer player = VisualizerModel.getInstance().getPlayer();
        player.playUri(MainActivity.operationCallback, VisualizerModel.getInstance().getTrackURI(), 0, 0);
        log.d("test", "TrackID: " + VisualizerModel.getInstance().getTrackURI());
    }


    private void setupVisualizer() {
        // Check Audio Record Permission
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "RECORD_AUDIO permission is required.", Toast.LENGTH_SHORT).show();

            }

            // If no permission then request it to the user
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSION);
            }

        } else {
            initVisualizer();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If Permission is granted then start the initialization
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initVisualizer();
                }
            }
        }
    }

    private void initVisualizer() {
        log.d("opengl", "In initVisualizer");
        int audioSampleSize = Visualizer.getCaptureSizeRange()[1];

        if(audioSampleSize > 512){
            audioSampleSize = 512;
        }

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        surfaceView = new VisualizerSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            log.d("opengl", "Supports ES2");
            // Request an OpenGL ES 2.0 compatible context.
            surfaceView.setEGLContextClientVersion(2);

            visualizerRenderer = new VisualizerRenderer(audioSampleSize);
            // Set the renderer to our demo renderer, defined below.
            surfaceView.setRenderer(visualizerRenderer, displayMetrics.density, audioSampleSize);
        }
        else
        {
            log.d("opengl", "Does not support ES2");
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }


//        mediaPlayer = MediaPlayer.create(this, R.raw.ritual);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.start();

        visualizer = new Visualizer(0);
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

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        // What in the fuck do i do with the waveform
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        surfaceView.updateFft(fft);
    }

}
