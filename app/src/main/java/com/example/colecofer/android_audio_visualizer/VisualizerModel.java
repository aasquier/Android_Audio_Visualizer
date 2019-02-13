package com.example.colecofer.android_audio_visualizer;

import android.content.BroadcastReceiver;
import android.graphics.Color;
import android.util.Log;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;

import static com.example.colecofer.android_audio_visualizer.Constants.MODEL_TAG;
import static com.example.colecofer.android_audio_visualizer.Constants.SWITCH_VIS_TIME;

public class VisualizerModel {

    //Spotify data
    private SpotifyPlayer player;
    private PlaybackState currentPlaybackState;
    private BroadcastReceiver networkStateReceiver;
    private String trackURI;
    String trackName;
    String artistName;
    String albumName;
    private int durationInMilliseconds;
    private int visualizerSwitchTimeOne;
    private int visualizerSwitchTimeTwo;
    public int colorMatrix[];

    //Visualizer / OpenGL instances
    private static final VisualizerModel visualizerModel = new VisualizerModel(); //VisualizerModel Singleton
    public static VisualizerRenderer renderer;         //TODO: Consider making these private
    public static VisualizerBase currentVisualizer;

    /**
     * Default Constructor
     */
    public VisualizerModel() {
        trackURI = "Not defined";
        trackName = "Not defined";
        artistName = "Not defined";
        albumName = "Not defined";
        colorMatrix = new int[] {0, 0, 0, 0};
    }


    /**
     * Allows access to the VisualizerModel Singleton outside of class scope.
     * @return The VisualizerModel singleton
     */
    public static VisualizerModel getInstance() { return visualizerModel; }

    public static VisualizerRenderer getRenderer() { return renderer; }

    public void setPlayer(SpotifyPlayer newPlayer) {
        this.player = newPlayer;
    }

    public SpotifyPlayer getPlayer() {
        return this.player;
    }

    public String getTrackURI() {
        return this.trackURI;
    }

    public void setTrackURI(String trackURI) {
        this.trackURI = trackURI;
    }

    public void setColors(int[] colors) {

        for(int i = 0; i < 3; ++i) {

            this.colorMatrix[i] = colors[i];
        }

        this.colorMatrix[3] = 1;

    }

    /**
     * Checks if it's time to switch visualizers, and if it's time
     * then changes currentVisualizer to the new visualizer
     */
    //TODO: This will only work with local files since it's based off the media player
    public void checkToSwitchVisualizer() {
        float currentTimeMillis = VisualizerActivity.mediaPlayer.getCurrentPosition();
        if (currentTimeMillis >= visualizerSwitchTimeOne && currentVisualizer.visNum == 1) {
            currentVisualizer.disableVertexAttribArrays();
            currentVisualizer = new VisTwo(VisualizerSurfaceView.context);
        }

        //TODO: Uncomment this when visualizer three is ready
        //else if (currentTimeMillis >= visualizerSwitchTimeTwo && currentVisualizer.visNum == 2) {
        //   currentVisualizer.disableVertexAttribArrays();
        //    currentVisualizer = new VisThree(VisualizerSurfaceView.context);
        //}
    }

    /**
     * Sets the times to switch visualizers
     * @param duration The length of the track in milliseconds
     */
    public void setDuration(int duration) {
        //TODO: This is temporarily being set to a constant defined in constants.java until things are working properly
        this.visualizerSwitchTimeOne = SWITCH_VIS_TIME;
        //durationInMilliseconds = duration;
        //visualizerSwitchTimeOne = duration / 3;
        //visualizerSwitchTimeTwo = visualizerSwitchTimeOne * 2;
    }

    /**
     * Removes the need to specify the TAG each time you log.
     * @param message The message to log
     */
    public void log(String message) { Log.d(MODEL_TAG, message);}

    public static void initRenderer(VisualizerRenderer inputRenderer) {
        renderer = inputRenderer;
    }

}
