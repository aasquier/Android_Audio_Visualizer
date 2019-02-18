package com.example.colecofer.android_audio_visualizer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Pair;


import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.example.colecofer.android_audio_visualizer.Constants.IMAGINARY_BUCKET_INDEX;
import static com.example.colecofer.android_audio_visualizer.Constants.MAX_FFT_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.REAL_BUCKET_INDEX;
import static com.example.colecofer.android_audio_visualizer.Constants.REQUEST_PERMISSION;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Utility.getDBs;
import static com.example.colecofer.android_audio_visualizer.Utility.updateDecibelHistory;
import static com.loopj.android.http.AsyncHttpClient.log;

public class VisualizerActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private static int fftArraySize;
    private long previousUpdateTime;
    static ConcurrentLinkedDeque<Float> decibelHistory;

    static MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private VisualizerSurfaceView surfaceView;
    private VisualizerRenderer visualizerRenderer;
    private Visualizer.OnDataCaptureListener captureListener;

    private TextView songTitle;
    private TextView artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide title bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_visualizer);
        //startTrackPlayback();  //Uncomment this line to start Spotify track playback
        ArrayList<Pair<Integer, String[]>> list = VisualizerModel.getInstance().getLyrics();

        Pair<Integer, String[]> pair = list.get(0);
        Log.d("test", "Entry: " + pair.first + " : " + pair.second);


        initDecibelHistory();
        initVisualizer();
        initUI();
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
    }

    /**
     * Setup the UI elements including the track Title, subtitile, and lyric text
     */
    private void initUI() {
        songTitle = new TextView(this);
        artistName = new TextView(this);

        //Text color
        songTitle.setTextColor(Color.WHITE);
        artistName.setTextColor(Color.WHITE);

        //Font size
        songTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
        artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);

        //Parameters for text views
        ViewGroup.MarginLayoutParams songMargin = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        ViewGroup.MarginLayoutParams artistMargin = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        songTitle.setLayoutParams(songMargin);
        artistName.setLayoutParams(artistMargin);

        //Ensure the title doesn't wrap and display an ellipsis if so
        songTitle.setEllipsize(TextUtils.TruncateAt.END);
        songTitle.setSingleLine(true);

        //Padding
        songTitle.setPadding(100, 100, 100, 100);
        artistName.setPadding(100, 200, 100, 100);

        //Create the Typeface objects
        Typeface titleTypeFace = ResourcesCompat.getFont(this, R.font.sofiaproblack);
        Typeface subtitleAndLyrics = ResourcesCompat.getFont(this, R.font.sofiaproextralight);

        //Set the custom fonts
        artistName.setTypeface(subtitleAndLyrics);
        songTitle.setTypeface(titleTypeFace);

        //Capitalize the title
        songTitle.setAllCaps(true);

        addContentView(songTitle, songMargin);
        addContentView(artistName, artistMargin);

        songTitle.requestLayout();
        artistName.requestLayout();
        this.setupLyricsUI();
    }


    private void setupLyricsUI() {


        TextView lyricsTV = new TextView(this);
        lyricsTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
        Typeface subtitleAndLyrics = ResourcesCompat.getFont(this, R.font.sofiaproextralight);
        lyricsTV.setTypeface(subtitleAndLyrics);


        //Setup the text and colors
        Spannable word = new SpannableString("Hah, sika than your average\n");

        //The first two value of the hex are opacity... So perhaps we could alter these to fade them in and out... ?
        word.setSpan(new ForegroundColorSpan(0x50FFFFFF), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        lyricsTV.setText(word);
        Spannable word1 = new SpannableString("Poppa twist cabbage off instinct");
        word1.setSpan(new ForegroundColorSpan(Color.RED), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        lyricsTV.append(word1);

        ViewGroup.MarginLayoutParams lyricsParams = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);
        lyricsTV.setLayoutParams(lyricsParams);
        lyricsTV.setPadding(100, 800, 100, 300);
        addContentView(lyricsTV, lyricsParams);
        lyricsTV.requestLayout();

        /////////////////////////
        //This adds a view over the surfaceview, but... you can't place it anywhere since margins doing work...
        //        View v = new View(this);
        //        v.setBackgroundColor(Color.BLUE);
        //        ViewGroup.MarginLayoutParams viewMargin = new ViewGroup.MarginLayoutParams(100, 100);
        //        viewMargin.setMargins(200, 200, 200, 200);
        //        v.setLayoutParams(viewMargin);
        //        v.setPadding(300, 300, 100, 100);
        //
        //        addContentView(v, viewMargin);
        //        v.requestLayout();


        /////////////////////Linear Layout attmpt///////////////////
        //        LinearLayout rlmain = new LinearLayout(this);
        //        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        //        LinearLayout   ll1 = new LinearLayout (this);
        //
        //        View iv = new View(this);
        //        iv.setBackgroundColor(Color.BLUE);
        //        LinearLayout .LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //
        //        iv.setLayoutParams(lp);
        //        ll1.addView(iv);
        //        rlmain.addView(ll1);
        //        setContentView(rlmain, llp);

        ///////////////// I have no idea I guess constraint attempt ////////////////////////////////
        //        ConstraintLayout rlmain = new ConstraintLayout(this);
        //        ConstraintLayout.LayoutParams llp = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //        ConstraintLayout   ll1 = new ConstraintLayout (this);
        //        ConstraintSet set = new ConstraintSet();
        //
        //        View iv = new View(this);
        //        iv.setBackgroundColor(Color.BLUE);
        //        ConstraintLayout .LayoutParams lp = new ConstraintLayout.LayoutParams(250, 200);
        //
        //        set.clone(rlmain);
        //        set.connect(iv.getId(), ConstraintSet.TOP, rlmain.getId(), ConstraintSet.BOTTOM, 150);
        //        set.applyTo(rlmain);
        //
        //        iv.setLayoutParams(lp);
        ////        ll1.setAlpha(0);
        //        ll1.addView(iv);
        //        rlmain.addView(ll1);
        //        setContentView(rlmain, llp);

        //k////////// Constraint attempt ///////////////
        //        ConstraintLayout mConstraintLayout  = (ConstraintLayout) findViewById(R.id.constraintLayout);
        //        mConstraintLayout = new ConstraintLayout(this);
        //        ConstraintSet set = new ConstraintSet();
        //
        //        View lyricsView = findViewById(R.id.lyricView);
        //        lyricsView = new View(this);
        //        mConstraintLayout.addView(lyricsView,0);
        //        set.clone(mConstraintLayout);
        //        set.connect(lyricsView.getId(), ConstraintSet.BOTTOM, mConstraintLayout.getId(), ConstraintSet.TOP, 60);
        //        set.applyTo(mConstraintLayout);



    }

    /**
     * Updates the opacity of the title textview between 0.5 and 1.0
     */
    private void animateTitleOpacity() {
        float alpha = 0.50f + decibelHistory.peekFirst() * 0.50f;
        this.songTitle.setAlpha(alpha);
    }

    /** Sets the decibel history to all 0.0 to begin with */
    private void initDecibelHistory() {
        this.decibelHistory = new ConcurrentLinkedDeque<>();
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
            this.animateTitleOpacity();
            VisualizerModel.getInstance().currentVisualizer.updateVertices();
        }
    }

    private void updateSongAndArtistName() {

        songTitle.setText(VisualizerModel.getInstance().trackName);
        artistName.setText(VisualizerModel.getInstance().artistName);

        artistName.setTextColor(VisualizerModel.getInstance().getColor(VisualizerModel.currentVisualizer.visNum - 1));
    }

}
