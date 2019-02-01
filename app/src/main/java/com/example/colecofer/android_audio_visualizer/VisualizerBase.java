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
abstract public class VisualizerBase {

    protected int positionHandle;
    protected int colorHandle;
    protected int captureSize;

}
