package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.AMPLIFIER;
import static com.example.colecofer.android_audio_visualizer.Constants.AMPLIFIER_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.DEFAULT_LINE_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.DEFAULT_LINE_SIZE_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.HIGH_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.MEDIUM_HIGHLIGHTING_PULSE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS3_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS3_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingDuration;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingHibernation;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingOnHigh;
import static com.example.colecofer.android_audio_visualizer.Utility.highlightingOnMedium;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class GLLineV3 {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float leftSide;
    private float rightSide;

    /**
     * Constructor
     * @param xPosition: Current line's base position
     */
    public GLLineV3(float xPosition) {

        this.leftSide = xPosition;   // Current line's left side coord
//        this.rightSide = leftSide + 0.001f;  // Current line's right side coord
        this.rightSide = leftSide;  // + 0.04f; Current line's right side coord


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

        this.vertices = new float[VIS3_ARRAY_SIZE];

        int vertexIndex = 0;
        float xAxis = -1.0f;
        float xOffset = (float) 2 / (DECIBEL_HISTORY_SIZE_V3) + 0.0018f;

        int visThreeIndex = 2;
        int visColor = VisualizerModel.getInstance().getColor(visThreeIndex);

        for(int i = 0; i < VIS3_ARRAY_SIZE; i+=14){
            // Left side
            this.vertices[vertexIndex] = xAxis;
            this.vertices[vertexIndex+1] = this.leftSide;
            this.vertices[vertexIndex+2] = 0.0f;
            this.vertices[vertexIndex+3] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);;
            this.vertices[vertexIndex+4] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+5] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+6] = 1.0f;

            // Right side
            this.vertices[vertexIndex+7] = xAxis;
            this.vertices[vertexIndex+8] = this.rightSide;
            this.vertices[vertexIndex+9] = 0.0f;
            this.vertices[vertexIndex+10] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);;
            this.vertices[vertexIndex+11] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+12] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+13] = 1.0f;

            // Next y coord
            xAxis += xOffset;
            vertexIndex+= (VERTEX_AMOUNT*2);
        }
    }

    /**
     * Update the base line with decibel value
     */
    public void updateVertices() {
        // Change to object array to traverse
        Float[] decibelFloatArray = decibelHistory.toArray(new Float[0]);

        int offset = 0;
        float highlightingFactor;
        float averageDecibels;

        // Only loop for the size of the decibel array size
        for(int i = 0; i < DECIBEL_HISTORY_SIZE_V3; i++) {
            // Calculate the coordinates after the amplification
            // Left side needs to move in negative direction
            // Right side needs to move in positive direction
            // Amplification should be half for both sides because Amplification = left + right

            switch(i) {
                case 0:                        averageDecibels = decibelFloatArray[0] + decibelFloatArray[1] + decibelFloatArray[2] + decibelFloatArray[3] + decibelFloatArray[4]; break;
                case 1:                        averageDecibels = decibelFloatArray[1] + decibelFloatArray[2] + decibelFloatArray[3] + decibelFloatArray[4] + decibelFloatArray[5]; break;
                case DECIBEL_HISTORY_SIZE - 2: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE - 6] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 5] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 4] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 2]; break;
                case DECIBEL_HISTORY_SIZE - 1: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE - 5] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 4] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 2] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 1]; break;
                default:                       averageDecibels = decibelFloatArray[i-2] + decibelFloatArray[i-1] + decibelFloatArray[i] + decibelFloatArray[i+1] + decibelFloatArray[i+2]; break;
            }

            averageDecibels /= 5.0f;

            if(averageDecibels <= 0.40) {
                highlightingFactor = 0.5f;
            } else if (averageDecibels <= 0.475) {
                highlightingFactor = 30.0f;
            } else if (averageDecibels <= 0.55){
                highlightingFactor = 50.0f;
            } else {
                highlightingFactor = 140.0f;
            }

            // V3 version
            float ampDataLeft  = (this.leftSide - (DEFAULT_LINE_SIZE_V3 + (AMPLIFIER_V3 * highlightingFactor)));
            float ampDataRight = (this.rightSide + (DEFAULT_LINE_SIZE_V3 + (AMPLIFIER_V3 * highlightingFactor)));

            this.vertices[offset+1] = ampDataLeft;
            this.vertices[offset+8] = ampDataRight;

            offset += 14;
        }

        FloatBuffer lineVerticesInput = ByteBuffer.allocateDirect(this.vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineVerticesInput.put(this.vertices).position(0);
        this.lineVerticesBuffer = lineVerticesInput;
    }

    public void updateVertices(boolean shouldUpdateHighlighting) {

        int xOffset = 0;
        float highlightingFactor;
        float averageDecibels;

        // Change to object array to traverse
        Float[] decibelFloatArray = decibelHistory.toArray(new Float[DECIBEL_HISTORY_SIZE_V3]);

        // Only loop for the size of the decibel array size
        for(int i = 0; i < DECIBEL_HISTORY_SIZE_V3; i++){
            // Calculate the coordinates after the amplification
            // Left side needs to move in negative direction
            // Right side needs to move in positive direction
            // Amplification should be half for both sides because Amplification = left + right

            // Takes the average of the three decibel levels surrounding the current y-position of the line in question
            switch(i) {
                case 0:                           averageDecibels = decibelFloatArray[0] + decibelFloatArray[1] + decibelFloatArray[2]; break;
                case DECIBEL_HISTORY_SIZE_V3 - 1: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE_V3 - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE_V3 - 2] + decibelFloatArray[DECIBEL_HISTORY_SIZE_V3 - 1]; break;
                default:                          averageDecibels = decibelFloatArray[i-1] + decibelFloatArray[i] + decibelFloatArray[i+1]; break;
            }

            averageDecibels /= 3.0f;

            if(averageDecibels <= 0.50f) {
                highlightingFactor = 0.5f;
            } else if (averageDecibels <= 0.60f) {
                highlightingFactor = 30.0f;
            } else if (averageDecibels <= 0.70f){
                if(!highlightingOnMedium && !highlightingOnHigh && !highlightingHibernation && shouldUpdateHighlighting){
                    highlightingOnMedium = true;
                    highlightingDuration = MEDIUM_HIGHLIGHTING_PULSE;
                }
                highlightingFactor = 50.0f;
            } else {
                if(!highlightingOnHigh && !highlightingOnMedium && !highlightingHibernation && shouldUpdateHighlighting) {
                    highlightingOnHigh = true;
                    highlightingDuration = HIGH_HIGHLIGHTING_PULSE;
                }
                highlightingFactor = 140.0f;
            }

            float ampDataLeft        = (this.leftSide - (DEFAULT_LINE_SIZE_V3 + AMPLIFIER_V3 * highlightingFactor));
            float ampDataRight       = (this.rightSide + (DEFAULT_LINE_SIZE_V3 + AMPLIFIER_V3 * highlightingFactor));
            this.vertices[xOffset+1] = ampDataLeft;
            this.vertices[xOffset+8] = ampDataRight;

            xOffset += 14;
        }

        FloatBuffer fftInput = ByteBuffer.allocateDirect(this.vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(this.vertices).position(0);
        this.lineVerticesBuffer = fftInput;
    }



    /**
     * Returns a floatbuffer of values to be drawn. (with timeHandle)
     */
    public void draw(int positionHandle, int colorHandle, int timeHandle, long startTime, int shouldMorphHandle, int shouldMorphToFractal) {
        /** Position Handle */
        this.lineVerticesBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        /** Color Handle */
        this.lineVerticesBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        /** Time Handle */
        GLES20.glUniform1f(timeHandle, (float)(System.currentTimeMillis() - startTime));

        /** dbLevel Handle */
        Float[] temp = decibelHistory.toArray(new Float[0]);

        float[] decibelsFloatArray = new float[temp.length];
        for (int i = 0; i < temp.length; ++i) {
            decibelsFloatArray[i] = temp[i] == null ? 0.0f : temp[i];
        }

        GLES20.glUniform1fv(VisualizerModel.getInstance().currentVisualizer.currentDecibelLevelHandle, decibelsFloatArray.length, decibelsFloatArray, 0);

        /** shouldMorphToFractalHandle */
        GLES20.glUniform1f(shouldMorphHandle, shouldMorphToFractal);

        /** finally draw buffer */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VIS3_VERTEX_COUNT);


    }
}