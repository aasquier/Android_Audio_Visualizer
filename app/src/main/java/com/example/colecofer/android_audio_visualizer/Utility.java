package com.example.colecofer.android_audio_visualizer;

public class Utility {

    /** Takes the real and imaginary parts of an FFT frequency bin and returns the decibels for that bin. */
    public static float getDBs(float real, float imaginary) {
        float normalizedBinMagnitude = 2.0f * (float) Math.sqrt(real*real+imaginary*imaginary) / 2.0f;

        return 20.0f * (float) Math.log10(normalizedBinMagnitude);
    }
}