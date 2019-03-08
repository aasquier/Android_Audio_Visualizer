package com.example.colecofer.android_audio_visualizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.example.colecofer.android_audio_visualizer.Constants.MODEL_TAG;
import static com.example.colecofer.android_audio_visualizer.Constants.SHOULD_LOOP_VIS;
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
    private int visualizerSwitchTime;
    private int lastSwitchTime;
    private ArrayList<Pair<Integer, String[]>> lyricList;
    public ArrayList<Integer> colorMatrix;


    //Visualizer / OpenGL instances
    private static final VisualizerModel visualizerModel = new VisualizerModel(); //VisualizerModel Singleton
    public static VisualizerRenderer renderer;         //TODO: Consider making these private
    public static VisualizerBase currentVisualizer;

    //Visualizer objects that the this.currentVisualizer object will point at
    public VisOne visOne;
    public VisTwo visTwo;
    public VisThree visThree;
    private int visCount;

    private ConcurrentLinkedQueue<VisualizerBase> visQueue;

    /**
     * Default Constructor
     */
    public VisualizerModel() {
        trackURI = "Not defined";
        trackName = "Not defined";
        artistName = "Not defined";
        albumName = "Not defined";
        colorMatrix = new ArrayList(3);
    }


    /**
     * Initialize the visualizers passing it the context.
     * They need the context for importing the glsl files.
     * This may not be optimal, but it's the only we found successful.
     * @param context The context for VisualizerSurfaceView
     */
    public void initVisualizers(Context context) {
        this.visOne = new VisOne(context);
        this.visTwo = new VisTwo(context);
        this.visThree = new VisThree(context);
        initVisualizerQueue();
    }

    /**
     * Initialize the queue of visualizers and use
     * the size of the queue to calculate certain fields
     */
    public void initVisualizerQueue() {
        visQueue = new ConcurrentLinkedQueue<>();
        visQueue.add(visOne);
//        visQueue.add(visTwo);
//        visQueue.add(visThree);

        visCount = visQueue.size();

        calculateSwitchTime();
    }

    /**
     * Removes and returns the first element in the queue.
     * If constant SHOULD_LOOP_VIS is set to true, first append
     * that element to the end of the queue
     * @return the first element in the queue
     */
    public VisualizerBase getNextVis() {
        if(SHOULD_LOOP_VIS) {
            visQueue.add(visQueue.peek());
        }
        return visQueue.poll();
    }

    /**
     * Allows other classes to peek at the next visualizer in line
     * @return
     */
    public VisualizerBase peekNextVis() {
        return visQueue.peek();
    }

    /**
     * Allows other classes to add a visualizer to the back of the queue
     * @param newVis
     */
    public void addVisToQueue(VisualizerBase newVis) {
        visQueue.add(newVis);
    }

    /**
     * Allows other classes to query the current size of the visualizer queue
     * @return
     */
    public int getVisQueueSize() {
        return visQueue.size();
    }

    /**
     * Checks if it's time to switch visualizers, and if it's time
     * then changes currentVisualizer to the new visualizer
     */
    //TODO: This will only work with local files since it's based off the media player
    public void checkToSwitchVisualizer() {
        int currentTimeMillis = VisualizerActivity.mediaPlayer.getCurrentPosition();

        if (currentTimeMillis > lastSwitchTime + visualizerSwitchTime) {
            lastSwitchTime = currentTimeMillis;
            currentVisualizer.disableVertexAttribArrays();
            currentVisualizer = getNextVis();
            VisualizerRenderer.initShaders();
        }
    }

    /**
     * Sets the times to switch visualizers
     * @param duration The length of the track in milliseconds
     */
    public void setDuration(int duration) {
        //TODO: This is temporarily being set to a constant defined in constants.java for debugging convenience
        durationInMilliseconds = duration;
//        visualizerSwitchTimeOne = duration / visCount;
//        visualizerSwitchTimeTwo = visualizerSwitchTimeOne * 2;
    }

    /**
     * Calculates how often visualizers should switch based on
     * the number of visualizers in the original queue
     */
    private void calculateSwitchTime() {
        //this.visualizerSwitchTime = durationInMilliseconds / visCount;
        this.visualizerSwitchTime = SWITCH_VIS_TIME;
        this.lastSwitchTime = 0;
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

        this.colorMatrix.clear();
        for(int i = 0; i < 3; ++i) {
            this.colorMatrix.add(colors[i]);
        }
        Collections.shuffle(colorMatrix);
    }

    public void setLyrics(ArrayList lyrics) {
        this.lyricList = lyrics;
    }

    public ArrayList<Pair<Integer, String[]>> getLyrics() {return this.lyricList;}


    /**
     * Removes the need to specify the TAG each time you log.
     * @param message The message to log
     */
    public void log(String message) { Log.d(MODEL_TAG, message);}

    public static void initRenderer(VisualizerRenderer inputRenderer) {
        renderer = inputRenderer;
    }

    public int getColor(int index) {
        if (index >= 0 && index < colorMatrix.size()) {
            return colorMatrix.get(index);
        }
        return colorMatrix.get(index);
    }

}
