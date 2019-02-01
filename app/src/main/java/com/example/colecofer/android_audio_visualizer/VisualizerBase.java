package com.example.colecofer.android_audio_visualizer;

/**
 * VisualizerBase is the fundamental abstract class that all visualizers should
 * be derived from. This way, we can have an object of this base class
 * and then assign it to derived visualizer instances, allowing us to
 * switch visualizers easily while a track is playing.
 *
 * Don't forget to also implement the GLInterface along with extending
 * this class.
 */
abstract public class VisualizerBase implements GLVisualizer {

    protected int positionHandle;
    protected int colorHandle;
    protected int captureSize;

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

}
