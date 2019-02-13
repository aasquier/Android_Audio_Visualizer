package com.example.colecofer.android_audio_visualizer;

import android.content.Context;

public class VisThree extends VisualizerBase {

    public VisThree(Context context) {
        this.visNum = 3;
    }

    /**
     * Initialization of handles during onSurfaceCreated in VisualizerRenderer
     */
    public void initOnSurfaceCreated(int positionHandle, int colorHandle) {
        this.positionHandle = positionHandle;
        this.colorHandle = colorHandle;
    }

    @Override
    public void updateVertices() {

    }

    @Override
    public void draw() {

    }
}
