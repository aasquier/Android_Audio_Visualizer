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
        VisualizerModel.getInstance().renderer = inputRenderer;
        VisualizerModel.getInstance().currentVisualizer = new VisOne(captureSize);

        super.setRenderer(VisualizerModel.getInstance().renderer);
    }

    public void updateFft(byte[] fft) {
        VisualizerModel.getInstance().currentVisualizer.updateFft(fft);
    }

}