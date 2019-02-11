package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;
import java.nio.FloatBuffer;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

/**
 * Class VisOne
 * This class extends VisualizerBase and overrides
 * updateVertices() and draw() methods so that openGL can
 * render it's contents.
 * */

//public class VisOne extends VisualizerBase {
public class VisOne extends VisualizerBase {

    private GLLine[] lines;  //Holds the lines to be displayed
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private Utility util;
    private float[] baseLineVertices;

    /**
     * Constructor
     */
    public VisOne(Context context) {
        lines = new GLLine[LINE_AMT];

        float k = LEFT_DRAW_BOUNDARY;

        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

        util = new Utility(context);

        // Create base line where we pass it into the line classes
        this.createBaseLine();

        this.vertexShader = util.getStringFromGLSL(R.raw.visonevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visonefragment);
    }

    public void createBaseLine(){
        this.baseLineVertices = new float[VIS1_ARRAY_SIZE];

        int vertexIndex = 0;
        float yAxis = -1.0f;
        float yOffset = (float) 2 / VIS1_VERTEX_COUNT;

        for(int i = 0; i < VIS1_ARRAY_SIZE; i+=7){
            // If left side of the line
            if(i % 2 == 0)
                this.baseLineVertices[vertexIndex] = 0.0f;
                // Else right side of the line
            else
                this.baseLineVertices[vertexIndex] = 0.0f + PIXEL;
            this.baseLineVertices[vertexIndex+1] = yAxis;
            this.baseLineVertices[vertexIndex+2] = 0.0f;

            // Uses retrieved color scheme to set the color
            this.baseLineVertices[vertexIndex+3] = 1.0f;
            this.baseLineVertices[vertexIndex+4] = 0.0f;
            this.baseLineVertices[vertexIndex+5] = 0.0f;
            this.baseLineVertices[vertexIndex+6] = 1.0f;

            yAxis += yOffset;
            vertexIndex+= VERTEX_AMOUNT;
        }
    }

    @Override
    public void updateVertices() {
        Object[] decibelArray = decibelHistory.toArray();

        int xOffset = 0;
        for(int i = 0; i < SCREEN_VERTICAL_HEIGHT; i++){
            this.baseLineVertices[xOffset] = PIXEL * (float)decibelArray[i];
            xOffset += 7;
        }
    }

    public void updateVertices(float[] newVertices) {
        for(int i = 0; i < LINE_AMT; i++){
            lines[i].updateLineVertex(newVertices);
        }
    }

    @Override
    public void draw() {
        //Go through each line and draw them
        for(int i = 0; i < LINE_AMT; ++i) {
            drawLine(lines[i].draw());
        }
    }

    /**
     * Draw a line given a set of verticies
     * @param lineVertexData
     */
    private void drawLine(FloatBuffer lineVertexData){
        lineVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        lineVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, VIS1_ARRAY_SIZE);
    }
}
