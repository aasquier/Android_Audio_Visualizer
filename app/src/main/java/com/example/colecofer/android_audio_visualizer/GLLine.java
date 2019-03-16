package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;

import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.HIGH_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.MEDIUM_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE_V1;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingOnHigh;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingOnMedium;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingDuration;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingHibernation;


public class GLLine {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float leftSide;
    private float rightSide;
    private float defaultLineSize;
    private float lineAmplifier;

    /**
     * Constructor
     * @param xPosition: Current line's base position
     */
    public GLLine(float xPosition, float lineSize, float amplifier) {

        this.leftSide        = xPosition;   // Current line's left side coord
        this.rightSide       = leftSide + PIXEL;  // Current line's right side coord
        this.defaultLineSize = lineSize;
        this.lineAmplifier   = amplifier;

        // Initialize the current line's base vertices
        createBaseLine();

        // Set up the FloatBuffer to draw before the onDataCapture kicks in
        lineVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineVerticesBuffer.put(vertices).position(0);
    }


    /**
     * Creating the base line vertices
     */
    public void createBaseLine(){

        this.vertices   = new float[VIS1_ARRAY_SIZE];
        int vertexIndex = 0;
        float yAxis     = -1.0f;
        float yOffset   = (float) 2 / (DECIBEL_HISTORY_SIZE_V1 - 1);
        int visOneIndex = 0;
        int visColor    = VisualizerModel.getInstance().getColor(visOneIndex);

        // Setting up right triangles
        for(int i = 0; i < VIS1_ARRAY_SIZE; i+=14){
            // Left side
            this.vertices[vertexIndex]   = this.leftSide;
            this.vertices[vertexIndex+1] = yAxis;
            this.vertices[vertexIndex+2] = 0.0f;
            this.vertices[vertexIndex+3] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+4] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+5] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+6] = 1.0f;

            // Right side
            this.vertices[vertexIndex+7]  = this.rightSide;
            this.vertices[vertexIndex+8]  = yAxis;
            this.vertices[vertexIndex+9]  = 0.0f;
            this.vertices[vertexIndex+10] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+11] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+12] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+13] = 1.0f;

            // Next y coord
            yAxis += yOffset;
            vertexIndex+= (VERTEX_AMOUNT*2);
        }
    }

    /**
     * Update the base line with decibel value
     */
    public void updateVertices(boolean shouldUpdateHighlighting) {

        int xOffset = 0;
        float highlightingFactor;
        float averageDecibels;
        float colorMult = 0.0f;

        //These numbers set the min and max that colorMult will scale between
        float colorMultMin = 0.6f;
        float colorMultMax = 1.0f;

        // Change to object array to traverse
        Float[] decibelFloatArray = decibelHistory.toArray(new Float[DECIBEL_HISTORY_SIZE_V1]);

        //Calculate the max and min of the decibel history values to normalize the data
        float min = 1.2f;
        float max = -0.1f;
        for (float decibel : decibelFloatArray) {
            if (decibel > max) max = decibel;
            if (decibel < min) min = decibel;
        }
        if (min < 0.0f) min = 0.0f;
        if (max > 1.0f) max = 1.0f;

        int visColor = VisualizerModel.getInstance().getColor(0);
//        Log.d("test", "Red: " + Color.red(visColor));
//        Log.d("test", "min: " + min);
//        Log.d("test", "max: " + max);
//        Log.d("test", "----------------------------");

        // Only loop for the size of the decibel array size
        for(int i = 0; i < DECIBEL_HISTORY_SIZE_V1; i++) {
            // Calculate the coordinates after the amplification
            // Left side needs to move in negative direction
            // Right side needs to move in positive direction
            // Amplification should be half for both sides because Amplification = left + right

            // Takes the average of the three decibel levels surrounding the current y-position of the line in question
            switch(i) {
                case 0:                        averageDecibels = decibelFloatArray[0] + decibelFloatArray[1] + decibelFloatArray[2]; break;
                case DECIBEL_HISTORY_SIZE_V1 - 1: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE_V1 - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE_V1 - 2] + decibelFloatArray[DECIBEL_HISTORY_SIZE_V1 - 1]; break;
                default:                       averageDecibels = decibelFloatArray[i-1] + decibelFloatArray[i] + decibelFloatArray[i+1]; break;
            }

            averageDecibels /= 3.0f;

            //Scale colorMult between colorMultMin and colorMultMax and use that as a multiplier
            //to calculate the new color for the current vertex
            colorMult = (((decibelFloatArray[i] - min) / (max - min)) * (colorMultMax - colorMultMin)) + colorMultMin;
            colorMult *= COLOR_SHIFT_FACTOR;

            if (xOffset + 26 < this.vertices.length) {
                this.vertices[xOffset + 3] = Color.red(visColor) * colorMult;
                this.vertices[xOffset + 4] = Color.green(visColor) * colorMult;
                this.vertices[xOffset + 5] = Color.blue(visColor) * colorMult;

                this.vertices[xOffset + 10] = Color.red(visColor) * colorMult;
                this.vertices[xOffset + 11] = Color.green(visColor) * colorMult;
                this.vertices[xOffset + 12] = Color.blue(visColor) * colorMult;
            }

            if(averageDecibels <= 0.55f) {

                highlightingFactor       = 20.0f;
                this.vertices[xOffset+2] = 0.0f;
                this.vertices[xOffset+9] = 0.0f;

            } else if (averageDecibels <= 0.6f) {

                highlightingFactor       = 30.0f;
                this.vertices[xOffset+2] = 0.0f;
                this.vertices[xOffset+9] = 0.0f;

            } else if (averageDecibels <= 0.65f){

                if(!highlightingOnMedium && !highlightingOnHigh && !highlightingHibernation && shouldUpdateHighlighting){
                    highlightingOnMedium = true;
                    highlightingDuration = MEDIUM_HIGHLIGHTING_PULSE;
                    highlightingFactor   = 25.0f;
                    this.vertices[xOffset+2] = 0.1f;
                    this.vertices[xOffset+9] = 0.1f;
                } else {
                    highlightingFactor = 45.0f;
                    this.vertices[xOffset+2] = 0.2f;
                    this.vertices[xOffset+9] = 0.2f;
                }

            } else {

                if(!highlightingOnHigh && !highlightingOnMedium && !highlightingHibernation && shouldUpdateHighlighting) {
                    highlightingOnHigh = true;
                    highlightingDuration = HIGH_HIGHLIGHTING_PULSE;
                    highlightingFactor = 45.0f;
                    this.vertices[xOffset+2] = 0.3f;
                    this.vertices[xOffset+9] = 0.3f;
                } else {
                    highlightingFactor = 75.0f;
                    this.vertices[xOffset+2] = 0.4f;
                    this.vertices[xOffset+9] = 0.4f;
                }
            }

            float ampDataLeft          = (this.leftSide - (this.defaultLineSize + this.lineAmplifier * highlightingFactor));
            float ampDataRight         = (this.rightSide + (this.defaultLineSize + this.lineAmplifier * highlightingFactor));
            this.vertices[xOffset]     = ampDataLeft;
            this.vertices[xOffset + 7] = ampDataRight;

            xOffset += 14;
        }

        FloatBuffer fftInput = ByteBuffer.allocateDirect(this.vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(this.vertices).position(0);
        this.lineVerticesBuffer = fftInput;
    }

    /**
     * Returns a floatbuffer of values to be drawn.
     */
    public void draw(int positionHandle, int colorHandle, Long visOneStartTime) {
        while (decibelHistory.peekFirst() == null) { continue; }

        this.lineVerticesBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        this.lineVerticesBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glUniform1f(VisualizerModel.getInstance().currentVisualizer.timeHandle, (float) (System.currentTimeMillis() - visOneStartTime));

        Float[] temp = decibelHistory.toArray(new Float[DECIBEL_HISTORY_SIZE_V1]);

        float[] dbs = new float[temp.length];
        for (int i = 0; i < DECIBEL_HISTORY_SIZE_V1; ++i) {
            dbs[i] = temp[i] == null ? 0.0f : temp[i];
        }

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1fv(VisualizerModel.getInstance().currentVisualizer.currentDecibelLevelHandle, dbs.length, dbs, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VIS1_VERTEX_COUNT);
    }
}