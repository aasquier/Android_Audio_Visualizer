package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_WIDTH;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_TIME;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS2_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class VisTwo extends VisualizerBase {

    private GLDot dot;
    private Utility util;
    private long visTwoStartTime;

    /**
     *
     * @param context
     */
    public VisTwo(Context context) {
        this.visNum = 2;
        util = new Utility(context);
        dot = new GLDot();

        this.vertexShader = util.getStringFromGLSL(R.raw.vistwovertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.vistwofragment);

        visTwoStartTime = System.currentTimeMillis();
    }

    /**
     * Initialization of handles during onSurfaceCreated in VisualizerRenderer
     */
    public void initOnSurfaceCreated(int positionHandle, int colorHandle, int programHandle) {
        this.positionHandle = positionHandle;
        this.colorHandle = colorHandle;
        this.currentDecibelLevelHandle = GLES20.glGetUniformLocation(programHandle, GLSL_DB_LEVEL);
        this.timeHandle = GLES20.glGetUniformLocation(programHandle, GLSL_TIME);
    }

    @Override
    public void updateVertices() {

    }

    @Override
    public void draw() {
        FloatBuffer dotVertexData = dot.draw();

        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendEquationSeparate(GLES20.GL_FUNC_ADD, GLES20.GL_FUNC_ADD);
//        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);


//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_COLOR);
//        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_COLOR, GLES20.GL_DST_COLOR, GLES20.GL_ZERO, GLES20.GL_ONE);

//
//        GLES20.glGenTextures(1, tex);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, DOT_WIDTH, DOT_HEIGHT, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, dotVertexData);


        /** Updates the position of individual dots for our screen rendering in the OpenGL pipeline */
        dotVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        /** Updates the color information for the dots rendered to the screen in the OpenGL pipeline */
        dotVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        Float[] temp = decibelHistory.toArray(new Float[0]);
        float[] dbs = new float[temp.length];
        for (int i = 0; i < temp.length; ++i) {
            dbs[i] = temp[i] == null ? 0.0f : temp[i];
        }

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1fv(currentDecibelLevelHandle, dbs.length, dbs, 0);

        GLES20.glUniform1f(timeHandle, (float)(System.currentTimeMillis() - visTwoStartTime));

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, DOT_COUNT);

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
