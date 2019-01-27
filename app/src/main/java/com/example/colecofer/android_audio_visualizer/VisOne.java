package com.example.colecofer.android_audio_visualizer;

public class VisOne {

    private final int SIZE = 100;
    private GLLine[] lines;
    private float[] tempColor = {1.0f, 0.0f, 0.0f, 1.0f};
    private float lineOffSet = 1.98f/99f;

    /**
     * Allocates 100 lines and offsets them on the x-axis
     * from -0.99 to +0.99.
     */
    public VisOne() {
        lines = new GLLine[100];
        float k = -0.99f;
        for(int i = 0; i < 100; ++i) {
            lines[i] = new GLLine(tempColor, k);
            k += lineOffSet;
        }
    }

    /**
     * Iterates through all 100 lines and calls the updateFft function.
     * @param fft
     */
    public void updateFft(float[] fft) {
        for(int i = 0; i < SIZE; ++i) {
            lines[i].updateFft(fft);
        }
    }

    /**
     * Iterates through all 100 lines and calls their draw function.
     */
    public void draw() {
        for(int i = 0; i < SIZE; ++i) {
            lines[i].draw();
        }
    }

}
