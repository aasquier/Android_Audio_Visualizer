package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLLine extends GLProgram {

    //Handles
    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;

    private FloatBuffer lineVertices;
    private float[] verticies;
    private float[] color;
    private float xOffset;

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


    public GLLine(float[] color, float xPosition) {
        this.color = color;
        this.xOffset = xPosition;

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
        int size = fft.length;
        for(int x = 0; x < size; x += 7) {
            fft[x] += xOffset;
        }

        //Puts the fft array into a FloatBuffer (drawable state for the GPU)
        FloatBuffer fftInput = ByteBuffer.allocateDirect(fft.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(fft).position(0);
        lineVertices = fftInput;
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
    private void draw() {
        lineVertices.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertices);
        GLES20.glEnableVertexAttribArray(positionHandle);

        lineVertices.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertices);
        GLES20.glEnableVertexAttribArray(colorHandle);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, VERTEX_COUNT);
    }


}
