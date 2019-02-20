package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class VisualizerSurfaceView extends GLSurfaceView {

    private Context context; //This exists so that we can read in the glsl files

    public VisualizerSurfaceView(Context context) {
        super(context);
        this.context = context;
    }

    public VisualizerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(VisualizerRenderer inputRenderer, float inputDensity, int vertexArraySize) {

        VisualizerModel.getInstance().renderer = inputRenderer;
        VisualizerModel.getInstance().initVisualizers(this.context);
        VisualizerModel.getInstance().currentVisualizer = VisualizerModel.getInstance().getNextVis();

        super.setRenderer(VisualizerModel.getInstance().renderer);
    }

}