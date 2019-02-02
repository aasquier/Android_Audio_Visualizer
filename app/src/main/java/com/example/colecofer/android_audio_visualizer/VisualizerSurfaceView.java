package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class VisualizerSurfaceView extends GLSurfaceView {

    private static float density;

    public VisualizerSurfaceView(Context context) {
        super(context);
    }

    public VisualizerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(VisualizerRenderer inputRenderer, float inputDensity, int captureSize) {
        this.density = inputDensity;

//        VisualizerModel.getInstance().visOne = new VisOne(captureSize);
        VisualizerModel.getInstance().visTwo = new VisTwo(captureSize);
        VisualizerModel.getInstance().renderer = inputRenderer;
        VisualizerModel.getInstance().currentVisualizer = VisualizerModel.getInstance().visTwo;

        super.setRenderer(inputRenderer);
    }

    public void updateFft(byte[] fft) {
        VisualizerModel.getInstance().currentVisualizer.updateFft(fft);
    }

}