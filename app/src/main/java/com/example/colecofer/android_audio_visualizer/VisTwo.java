package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_COUNT;
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

    // TODO We may want to consider moving the "drawDot" logic into this function, it seems to be serving no real purpose
    @Override
    public void draw() {
        drawDot(dot.draw(), DOT_COUNT);
    }

    private void drawDot(FloatBuffer dotVertexData, int count) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        /** Updates the position of individual dots for our screen rendering in the OpenGL pipeline */
        dotVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        /** Updates the color information for the dots rendered to the screen in the OpenGL pipeline */
        dotVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        Iterator iterator = decibelHistory.iterator();
        float[] dbs = new float[SCREEN_VERTICAL_HEIGHT];
        int i = 0;
        while(iterator.hasNext()) {
            dbs[i] = (float)iterator.next();
            i++;
        }

//        ByteBuffer dbByteBuffer = ByteBuffer.allocateDirect(dbs.length * 4);
//        dbByteBuffer.order(ByteOrder.nativeOrder());
//        FloatBuffer dbsBuffer = dbByteBuffer.asFloatBuffer();
//        dbsBuffer.put(dbs).position(0);

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1fv(currentDecibelLevelHandle, SCREEN_VERTICAL_HEIGHT, dbs, 0);

        GLES20.glUniform1f(timeHandle, (float)(System.currentTimeMillis() - visTwoStartTime));

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);

        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
