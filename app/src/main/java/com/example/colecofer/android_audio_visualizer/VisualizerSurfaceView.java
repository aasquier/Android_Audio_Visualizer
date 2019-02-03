package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


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
        float currentTimeMillis = VisualizerActivity.mediaPlayer.getCurrentPosition();
        if (currentTimeMillis >= 2000 && VisualizerModel.getInstance().currentVisualizer.visNum == 1) {
            Log.d("test", "Switch vis");
            VisualizerModel.getInstance().currentVisualizer = new VisTwo(1024);
        } else if (currentTimeMillis >= 4000 && VisualizerModel.getInstance().currentVisualizer.visNum == 2) {
//            this.currentVisualizer = new VisThree(this.currentVisualizer.captureSize);
        }

        VisualizerModel.getInstance().currentVisualizer.updateFft(fft);
    }

}