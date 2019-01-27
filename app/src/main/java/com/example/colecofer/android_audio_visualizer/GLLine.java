package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

public class GLLine {

    //Handles
    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;

    private float[] verticies;
    private float[] color;
    private float xPosition;

    static final int VERTEX_COUNT = 2;
    static final int POSITION_DATA_SIZE = 3;
    static final int BYTES_PER_FLOAT = 4;
    static final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;
    static final int POSITION_OFFSET = 0;
    static final int COLOR_OFFSET = 3;
    static final int COLOR_DATA_SIZE = 4;


    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];

    final String vertexShader =
        "uniform mat4 u_MVPMatrix;" +
        "attribute vec4 a_Position;" +
        "attribute vec4 a_Color;" +
        "varying vec4 v_Color;" +
        "void main()" +
        "{" +
        "   v_Color = a_Color;" +
        "   gl_Position = u_MVPMatrix" +
        "               * a_Position;" +
        "}";

    final String fragmentShader =
        "precision mediump float;" +
        "varying vec4 v_Color;" +
        "void main()" +
        "{" +
        "   gl_FragColor = v_Color;" +
        "}";


    //TODO: Check if yPosition is within screen bounds before settting?
    public GLLine(float[] color, float xPosition) {
        this.color = color;
        this.xPosition = xPosition;

        verticies = new float[VERTEX_COUNT];

        //Bottom point of line
        verticies[0] = xPosition;
        verticies[1] = 0;
        verticies[2] = 0;

        //Top point of line
        verticies[3] = xPosition;
        verticies[4] = 0;
        verticies[4] = 0;
    }


    /**
     * Modifies the passed in fft which is just an amplification value
     * of the current line's x-axis.
     * Converts the array into a FloatBuffer for efficiency (can be used
     * in the GPU)
     * @param fft
     */
    private void updateFft(float[] fft) {

    }


    /**
     * This method should be invoked when Visualizer One catches the pulsating flag.
     * Do a plus and minus (?) to get the range of the highlight.
     * Change the float array in the y-axis range by the colorCode.
     * Amplify the reaction by the pulseAmp variable to give it a larger amplification.
     * @param colorCode
     * @param yAxis
     */
    private void highlightPulse(float[] colorCode, float yAxis) {

    }


    /**
     * Draw the line given a set of vertices
     */
    private void draw(FloatBuffer lineVertexData) {
        lineVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        lineVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, VERTEX_COUNT);
    }

    /**
     * Set the color for the line given individual color values
     * This function might not be necessary
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }

    }
