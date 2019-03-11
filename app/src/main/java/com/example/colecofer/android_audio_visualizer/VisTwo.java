package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;

import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_MATRIX;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_SCREEN_RATIO;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_TIME;


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
        this.screenRatioHandle = GLES20.glGetUniformLocation(programHandle, GLSL_SCREEN_RATIO);
    }

    @Override
    public void updateVertices() {

    }

    @Override
    public void draw(float[] mvpMatrix) {
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;

        // Setting the screen ratio of device to keep v2 circle shape
        GLES20.glUniform1f(screenRatioHandle, (float)height/width);

        this.dot.draw(this.positionHandle, this.colorHandle, this.timeHandle, this.currentDecibelLevelHandle, this.visTwoStartTime);
    }
}
