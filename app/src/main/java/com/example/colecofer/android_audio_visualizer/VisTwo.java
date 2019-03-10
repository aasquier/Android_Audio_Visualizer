package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;

import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE_V2;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_MATRIX;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_TIME;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;


public class VisTwo extends VisualizerBase {

    private GLDot dot;
    private Utility util;
    private long visTwoStartTime;

    /**
     *
     * @param context
     */
     VisTwo(Context context) {
        this.visNum = 2;
        this.util = new Utility(context);
        this.dot = new GLDot();

        this.vertexShader = util.getStringFromGLSL(R.raw.vistwovertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.vistwofragment);

        visTwoStartTime = System.currentTimeMillis();

    }

    /**
     * Initialization of handles during onSurfaceCreated in VisualizerRenderer
     */
    void initOnSurfaceCreated(int positionHandle, int colorHandle, int programHandle) {
        this.positionHandle = positionHandle;
        this.colorHandle = colorHandle;
        this.currentDecibelLevelHandle = GLES20.glGetUniformLocation(programHandle, GLSL_DB_LEVEL);
        this.timeHandle = GLES20.glGetUniformLocation(programHandle, GLSL_TIME);
        this.matrixHandle = GLES20.glGetUniformLocation(programHandle, GLSL_MATRIX);
    }

    @Override
    public void updateVertices() {

    }

    @Override
    public void draw(float[] mvpMatrix) {
        this.dot.draw(this.positionHandle, this.colorHandle, this.timeHandle, this.currentDecibelLevelHandle, this.visTwoStartTime);
    }
}
