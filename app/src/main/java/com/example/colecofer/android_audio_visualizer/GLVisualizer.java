package com.example.colecofer.android_audio_visualizer;

/**
 * All custom visualizers should implement this interface.
 */

public interface GLVisualizer {

    /**
     * Called from the Surface View and should setup the initial fft values.
     * @param fft
     */
    public void updateFft(byte[] fft);

    /**
     * Called from the Renderer and should be used to update animations
     * @param fft
     */
    public void updateFft(float[] fft);

    /**
     * This method will be in charge of calling the individual draw() methods
     * of other items that need to be rendered.
     */
    public void draw();

}
