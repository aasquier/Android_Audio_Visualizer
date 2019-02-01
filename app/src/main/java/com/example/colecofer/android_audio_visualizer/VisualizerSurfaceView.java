package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;


public class VisualizerSurfaceView extends GLSurfaceView {

    private static float density;
    //public static VisualizerRenderer renderer;

    public VisualizerSurfaceView(Context context) {
        super(context);
    }

    public VisualizerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(VisualizerRenderer inputRenderer, float inputDensity, int captureSize) {
        this.density = inputDensity;
        VisualizerModel.getInstance().initRenderer(inputRenderer);
        VisualizerModel.getInstance().visOne = new VisOne(captureSize);
        VisualizerModel.getInstance().currentVisualizer = VisualizerModel.getInstance().visOne;

        //this.renderer = inputRenderer;
        //this.visOne = new VisOne(captureSize);

        super.setRenderer(inputRenderer);
    }

    public void updateFft(byte[] fft) {
        VisualizerModel.getInstance().currentVisualizer.updateFft(fft);
        //visOne.updateFft(fft);

    }

}