package com.example.colecofer.android_audio_visualizer;

import java.util.ArrayDeque;

/**
 * VisualizerBase is the fundamental abstract class that all visualizers should
 * be derived from. This way, we can have an object of this base class
 * and then assign it to derived visualizer instances, allowing us to
 * switch visualizers easily while a track is playing.
 */
abstract public class VisualizerBase {

    private final int SCREEN_SIZE = 1024;

    protected ArrayDeque<Float> dbHistory;
    protected int positionHandle;
    protected int colorHandle;
    protected int captureSize;

    /**
     * Default Constructor
     */
    public VisualizerBase() {
        this.dbHistory = new ArrayDeque<>();
        for(int i = 0; i < SCREEN_SIZE; ++i) {
            this.dbHistory.addFirst(0.0f);
        }
    }

    /**
     * Set the position handle
     * This is necessary so that the renderer can update the position handle
     * @param positionHandle
     */
    public void setPositionHandle(int positionHandle) { this.positionHandle = positionHandle; }

    /**
     * Set the color handle
     * This is necessary so that the renderer can update the color handle
     * @param colorHandle
     */
    public void setColorHandle(int colorHandle) { this.colorHandle = colorHandle; }

    /**
     * Called from the Surface View and should setup the initial fft values.
     * @param fft
     */
    abstract public void updateFft(byte[] fft);

    /**
     * Called from the Renderer and should be used to update animations
     * @param fft
     */
    abstract public void updateFft(float[] fft);

    /**
     * This method will be in charge of calling the individual draw() methods
     * of other items that need to be rendered.
     */
    abstract public void draw();


}