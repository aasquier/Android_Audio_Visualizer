package com.example.colecofer.android_audio_visualizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Pair;


import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayDeque;

import static com.example.colecofer.android_audio_visualizer.Constants.IMAGINARY_BUCKET_INDEX;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_FFT_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.REAL_BUCKET_INDEX;
import static com.example.colecofer.android_audio_visualizer.Constants.REQUEST_PERMISSION;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_PADDING;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Utility.getDBs;
import static com.example.colecofer.android_audio_visualizer.Utility.updateDecibelHistory;
import static com.loopj.android.http.AsyncHttpClient.log;

public class VisualizerActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private static int fftArraySize;
    private long previousUpdateTime;
    static ArrayDeque<Float> decibelHistory;

    static MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private VisualizerSurfaceView surfaceView;
    private VisualizerRenderer visualizerRenderer;
    private Visualizer.OnDataCaptureListener captureListener;

    //Title UI Elements
    private TextView songTitle;
    private TextView artistName;

    //Lyrics Animation Elements
    private View lyricsView2;
    private LyricsView lyricsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        //startTrackPlayback();  //Uncomment this line to start Spotify track playback
        initDecibelHistory();
        initVisualizer();
    }

    /**
     * Invokes track playback using the SpotifyPlayer in VisualizerModel
     */
    private void startTrackPlayback() {
        SpotifyPlayer player = VisualizerModel.getInstance().getPlayer();
        player.playUri(MainActivity.operationCallback, VisualizerModel.getInstance().getTrackURI(), 0, 0);
        log.d("test", "TrackID: " + VisualizerModel.getInstance().getTrackURI());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                //If Permission is granted then start the initialization
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initVisualizer();
                }
            }
        }
    }

    /**
     * Initializes the visualizer.
     * This function currently uses locally stored files because we are blocked
     * at getting the visualizer to listen to all audio output which is accomplished
     * when passing a 0 to the Visualizer constructor. This is necessary to use Spotify.
     */
    private void initVisualizer() {
        this.fftArraySize = Visualizer.getCaptureSizeRange()[1];

        /** This ends up being the max size we will use, it is actually half of this number that defines
         *  how many buckets we can have. So we have 512 "Frequency buckets" if this is 1024 to account
         *  for the real and imaginary parts of each bucket. There is a frequency range of 0-20000 HZ.
         *  This gives us a frequency granularity of 39.06 Hz, so our target will be the 3rd bucket for
         *  the real component, and 4th bucket for the imaginary component which covers 78.12-117.18 Hz
         */
        if (this.fftArraySize > MAX_FFT_ARRAY_SIZE) {
            this.fftArraySize = MAX_FFT_ARRAY_SIZE;
        }

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        surfaceView = new VisualizerSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        //Check if ES2 is supported on the device
        if (supportsEs2) {
            surfaceView.setEGLContextClientVersion(2);
            visualizerRenderer = new VisualizerRenderer();
            surfaceView.setRenderer(visualizerRenderer, displayMetrics.density, fftArraySize);
        } else {
            log.d("opengl", "Does not support ES2");
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        //Sets up the visualizer for local files
        mediaPlayer = MediaPlayer.create(this, R.raw.jazz);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(fftArraySize);
        visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        visualizer.setEnabled(true);

        this.previousUpdateTime = System.currentTimeMillis();

        setContentView(surfaceView);

        // Add song and artist text view to the visualizer
        songTitle = new TextView(this);
        artistName = new TextView(this);

        songTitle.setTextColor(Color.WHITE);
        artistName.setTextColor(Color.WHITE);
        songTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
        artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

        ViewGroup.MarginLayoutParams songMargin = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
        ViewGroup.MarginLayoutParams artistMargin = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);

        songTitle.setLayoutParams(songMargin);
        songTitle.setPadding(LEFT_PADDING, 100, RIGHT_PADDING, 100);
        addContentView(songTitle, songMargin);

        artistName.setLayoutParams(artistMargin);
        artistName.setPadding(LEFT_PADDING, 200, RIGHT_PADDING, 100);
        addContentView(artistName, artistMargin);

        songTitle.requestLayout();
        artistName.requestLayout();


        ///////////////////////////////////




//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
//

//
//        TextView tv = new TextView(this);
//        tv.setTextColor(Color.RED);
//        tv.setText("THIS IS A TEST");
//
//        View v = new View(this);
//        v.setBackgroundColor(Color.BLUE);
////        v.setPadding(100, 100, 100, 100);
//
//        View v2 = new View(this);
//        v2.setBackgroundColor(Color.RED);
//        v2.setPadding(100, 100, 100, 100);
//
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        rmain.addView(v, params);
//        setContentView(rmain, params);
/////////////////////////////////

//        LinearLayout rlmain = new LinearLayout(this);
//        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
//        LinearLayout   ll1 = new LinearLayout (this);
//        rlmain.setGravity(100);

//        ll1.setGravity(1);

//        ImageView iv = new ImageView(this);
//        iv.setImageResource(R.drawable.logo);
//        TextView iv = new TextView(this);
//        iv.setText("THIS IS A TEST TEXTTTTT");

//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
//        View iv = new View(this);
//        iv.setBackgroundColor(Color.BLUE);
//        iv.setPadding(0, 0, 0,  0);
//
//        View iv2 = new View(this);
//        iv2.setBackgroundColor(Color.GREEN);
//        iv2.setPadding(0, 0, 0, 0);
//
//
//        //Layout params
//        LinearLayout .LayoutParams lp = new LinearLayout.LayoutParams(600, 600);
//
//
//        //Add the view onto the layout
//        iv.setLayoutParams(lp);
//        ll1.addView(iv);
//        ll1.addView(iv2);
//
//        rlmain.addView(ll1);
//        setContentView(rlmain, llp);

        ///////////////////////////////////////////



//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//
////        //Add the lyrics view onto the visualizer activity
//        lyricsView = new LyricsView(this, screenWidth, screenHeight);
//
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(599, 600);
//        ViewGroup.MarginLayoutParams lyricsMargin = new ViewGroup.MarginLayoutParams(900, 900);
//
//        lyricsView.setLayoutParams(params);
//        lyricsView.setBackgroundColor(Color.BLUE);
//        lyricsView.setPadding(100, 300, 100, 300);
//        addContentView(lyricsView, lyricsMargin);

    }


    /** Sets the decibel history to all 0.0 to begin with */
    private void initDecibelHistory() {
        this.decibelHistory = new ArrayDeque<>();
        for(int i = 0; i < SCREEN_VERTICAL_HEIGHT; ++i) {
            this.decibelHistory.addFirst(0.0f);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        this.mediaPlayer.release();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
      
        /** Gives us the decibel level for the fft bucket we care about **/
        double currentDecibels = getDBs(fft[REAL_BUCKET_INDEX], fft[IMAGINARY_BUCKET_INDEX], this.fftArraySize);
        updateSongAndArtistName();
        /** Check and see if it is time to update the decibel history with the current decibel level, and check if it is time to
         *  refresh the screen based on our 60 fps */
        Pair<Long, Boolean> isTimeToRefreshScreen = updateDecibelHistory(currentDecibels, this.previousUpdateTime);


        /** Update the screen if the elapsed time has exceeded the threshold set */
        if(isTimeToRefreshScreen.second) {
            VisualizerModel.getInstance().currentVisualizer.updateVertices();
        }
    }

    private void updateSongAndArtistName() {

        songTitle.setText(VisualizerModel.getInstance().trackName);
        artistName.setText(VisualizerModel.getInstance().artistName);
    }

}
