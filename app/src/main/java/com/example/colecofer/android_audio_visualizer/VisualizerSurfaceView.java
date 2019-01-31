package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class VisualizerSurfaceView extends GLSurfaceView {

    private static float density;
    public static VisOne visOne;
    public static VisualizerRenderer renderer;

    public VisualizerSurfaceView(Context context) {
        super(context);
    }

    public VisualizerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(VisualizerRenderer inputRenderer, float inputDensity, int captureSize) {
        this.renderer = inputRenderer;
        this.density = inputDensity;
        this.visOne = new VisOne(captureSize);

        super.setRenderer(inputRenderer);
    }

    public void updateFft(byte[] fft) {
        visOne.updateFft(fft);

    }

}