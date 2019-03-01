package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.AMPLIFIER;
import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.DEFAULT_LINE_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.DECIBEL_HISTORY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class GLLine {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float[] scalingLevel;
    private FloatBuffer scalingLevelBuffer;
    private float leftSide;
    private float rightSide;

    /**
     * Constructor
     * @param xPosition: Current line's base position
     */
    public GLLine(float xPosition) {

        this.leftSide = xPosition;   // Current line's left side coord
        this.rightSide = leftSide + PIXEL;  // Current line's right side coord

        // TODO This is to possibly pass in to the shader
        // The size need to match the vertex array size because it'll be an attribute variable
        this.scalingLevel = new float[DECIBEL_HISTORY_SIZE*2];

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

        this.vertices = new float[VIS1_ARRAY_SIZE];

        int vertexIndex = 0;
        float yAxis = -1.0f;
        float yOffset = (float) 2 / (DECIBEL_HISTORY_SIZE - 1);
        int visOneIndex = 0;
        int visColor = VisualizerModel.getInstance().getColor(visOneIndex);

        // Setting up right triangles
        for(int i = 0; i < VIS1_ARRAY_SIZE; i+=14){
            // Left side
            this.vertices[vertexIndex] = this.leftSide;
            this.vertices[vertexIndex+1] = yAxis;
            this.vertices[vertexIndex+2] = 0.0f;
            this.vertices[vertexIndex+3] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+4] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+5] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+6] = 1.0f;

            // Right side
            this.vertices[vertexIndex+7] = this.rightSide;
            this.vertices[vertexIndex+8] = yAxis;
            this.vertices[vertexIndex+9] = 0.0f;
            this.vertices[vertexIndex+10] = (Color.red(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+11] = (Color.green(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+12] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR);
            this.vertices[vertexIndex+13] = 1.0f;

            // Next y coord
            yAxis += yOffset;
            vertexIndex+= (VERTEX_AMOUNT*2);
        }

        for(int i = 0; i < this.scalingLevel.length; i++){
            this.scalingLevel[i] = 0.0f;
        }

        FloatBuffer scaleInput = ByteBuffer.allocateDirect(this.scalingLevel.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        scaleInput.put(this.scalingLevel).position(0);
        this.scalingLevelBuffer = scaleInput;
    }

    /**
     * Update the base line with decibel value
     */
    public void updateVertices() {

        // Change to object array to traverse
        Float[] decibelFloatArray = decibelHistory.toArray(new Float[DECIBEL_HISTORY_SIZE]);

        int xOffset = 0;

        float highlightingFactor = 1.0f;
        float averageDecibels;
        int scalerIndex = 0;

        // Only loop for the size of the decibel array size
        for(int i = 0; i < DECIBEL_HISTORY_SIZE; i++){
            // Calculate the coordinates after the amplification
            // Left side needs to move in negative direction
            // Right side needs to move in positive direction
            // Amplification should be half for both sides because Amplification = left + right

            // Takes the average of the five decibel levels surrounding the current y-position of the line in question
            switch(i) {
                case 0:                        averageDecibels = decibelFloatArray[0] + decibelFloatArray[1] + decibelFloatArray[2] + decibelFloatArray[3] + decibelFloatArray[4]; break;
                case 1:                        averageDecibels = decibelFloatArray[1] + decibelFloatArray[2] + decibelFloatArray[3] + decibelFloatArray[4] + decibelFloatArray[5]; break;
                case DECIBEL_HISTORY_SIZE - 2: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE - 6] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 5] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 4] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 2]; break;
                case DECIBEL_HISTORY_SIZE - 1: averageDecibels = decibelFloatArray[DECIBEL_HISTORY_SIZE - 5] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 4] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 3] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 2] + decibelFloatArray[DECIBEL_HISTORY_SIZE - 1]; break;
                default:                       averageDecibels = decibelFloatArray[i-2] + decibelFloatArray[i-1] + decibelFloatArray[i] + decibelFloatArray[i+1] + decibelFloatArray[i+2]; break;
            }

            // TODO placeholder
            averageDecibels /= 5.0f;

            // Adding scaler value depending on the decibel
            // And it needs two because there are two points per y-axis
            // The value that's being initialized needs to be played with to have a smoother or more better looking visualizer
            if(averageDecibels <= 0.55) {
                highlightingFactor = 10.0f;
                this.scalingLevel[scalerIndex] = 0.015f;
                this.scalingLevel[scalerIndex+1] = 0.015f;
            } else if (averageDecibels <= 0.6) {
                highlightingFactor = 50.0f;
                this.scalingLevel[scalerIndex] = 0.02f;
                this.scalingLevel[scalerIndex+1] = 0.02f;
//            } else if (averageDecibels <= 0.6) {
//                highlightingFactor = 55.0f;
//                this.scalingLevel[scalerIndex] = 0.55f;
//                this.scalingLevel[scalerIndex+1] = 0.55f;
//            } else if (averageDecibels <= 0.6) {
//                highlightingFactor = 55.0f;
//                this.scalingLevel[scalerIndex] = 0.5f;
//                this.scalingLevel[scalerIndex+1] = 0.5f;
//            } else if (averageDecibels <= 0.65){
//                highlightingFactor = 85.0f;
//                this.scalingLevel[scalerIndex] = 0.65f;
//                this.scalingLevel[scalerIndex+1] = 0.65f;
            } else {
                highlightingFactor = 100.0f;
                this.scalingLevel[scalerIndex] = 0.03f;
                this.scalingLevel[scalerIndex+1] = 0.03f;
            }

            float ampDataLeft = (this.leftSide - (DEFAULT_LINE_SIZE + AMPLIFIER * highlightingFactor));
            float ampDataRight = (this.rightSide + (DEFAULT_LINE_SIZE + AMPLIFIER * highlightingFactor));
            this.vertices[xOffset] = ampDataLeft;
            this.vertices[xOffset+2] = scalingLevel[scalerIndex];

            this.vertices[xOffset+7] = ampDataRight;
            this.vertices[xOffset+9] = scalingLevel[scalerIndex+1];

            xOffset += 14;
            scalerIndex += 2;
        }


        FloatBuffer fftInput = ByteBuffer.allocateDirect(this.vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(this.vertices).position(0);
        this.lineVerticesBuffer = fftInput;

        // Setting FloatBuffer for the scaler
        FloatBuffer scaleInput = ByteBuffer.allocateDirect(this.scalingLevel.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        scaleInput.put(this.scalingLevel).position(0);
        this.scalingLevelBuffer = scaleInput;
    }

    /**
     * Returns a floatbuffer of values to be drawn.
     */
    public void draw(int positionHandle, int colorHandle, Long visOneStartTime) {
        while (decibelHistory.peekFirst() == null) { continue; }
        while (scalingLevelBuffer == null) { continue; }

        this.lineVerticesBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        this.lineVerticesBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glUniform1f(VisualizerModel.getInstance().currentVisualizer.timeHandle, (float) (System.currentTimeMillis() - visOneStartTime));

        Float[] temp = decibelHistory.toArray(new Float[DECIBEL_HISTORY_SIZE]);

        float[] dbs = new float[temp.length];
        for (int i = 0; i < DECIBEL_HISTORY_SIZE; ++i) {
            dbs[i] = temp[i] == null ? 0.0f : temp[i];

            // TODO this was to possibly send in a negative value randomly
//            if (Math.random() > 0.5) {
//                dbs[i] *= -1;
//            }
        }

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1fv(VisualizerModel.getInstance().currentVisualizer.currentDecibelLevelHandle, dbs.length, dbs, 0);

        // Pushing scaler buffer up to the GPU for Attribute variable
//        GLES20.glUniform1fv(VisualizerModel.getInstance().currentVisualizer.scalingLevelArrayHandle, this.scalingLevel.length, this.scalingLevel, 0);
        GLES20.glVertexAttribPointer(VisualizerModel.getInstance().currentVisualizer.scalingLevelArrayHandle, 1, GLES20.GL_FLOAT, false, BYTES_PER_FLOAT, this.scalingLevelBuffer);
        GLES20.glEnableVertexAttribArray(VisualizerModel.getInstance().currentVisualizer.scalingLevelArrayHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VIS1_VERTEX_COUNT);
    }
}