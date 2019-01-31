package com.example.colecofer.android_audio_visualizer;

/**
 * All custom visualizers should implement this interface.
 */

public interface GLVisualizer {

    public void updateFft(float[] fft);
    public void draw(int program);

}
