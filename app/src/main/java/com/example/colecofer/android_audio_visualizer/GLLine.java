package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;

import static com.example.colecofer.android_audio_visualizer.Constants.AMPLIFIER;
import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.DEFAULT_LINE_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS3_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS3_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class GLLine {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float leftSide;
    private float rightSide;

    /**
     * Constructor
     * @param xPosition: Current line's base position
     */
    public GLLine(float xPosition) {

        this.leftSide = xPosition;   // Current line's left side coord
        this.rightSide = leftSide + PIXEL;  // Current line's right side coord

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
        float yOffset = (float) 2 / (SCREEN_VERTICAL_HEIGHT - 1);
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
    }

    /**
     * Update the base line with decibel value
     */
    public void updateVertices() {
        // Change to object array to traverse
        Object[] decibelArray = decibelHistory.toArray();

        int xOffset = 0;

        float currentDecibel = 0.0f;
        float db = 0.0f;

        // Only loop for the size of the decibel array size
        for(int i = 0; i < SCREEN_VERTICAL_HEIGHT; i++){
            // Calculate the coordinates after the amplification
            // Left side needs to move in negative direction
            // Right side needs to move in positive direction
            // Amplification should be half for both sides because Amplification = left + right

            // Not sure about the full algorithm with if and else statement here
            // Will come back to it later
            //TODO: Figure out what is going on with this algorithm

//            float currentDecibel = (float) decibelArray[i] <= 0.5f ? 15.0f : 170.0f;
            db = (float) decibelArray[i];

            if(db <= 0.4) {
                currentDecibel = 1.0f;
            } else if (db <= 0.66) {
                currentDecibel = 30.0f;
            } else {
                currentDecibel = 160.0f;
            }


            float ampDataLeft = (this.leftSide - (DEFAULT_LINE_SIZE + AMPLIFIER * currentDecibel));
            float ampDataRight = (this.rightSide + (DEFAULT_LINE_SIZE + AMPLIFIER * currentDecibel));
            this.vertices[xOffset] = ampDataLeft;
            this.vertices[xOffset+7] = ampDataRight;

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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VIS1_VERTEX_COUNT);

        GLES20.glUniform1f(VisualizerModel.getInstance().currentVisualizer.timeHandle, (float) (System.currentTimeMillis() - visOneStartTime));

        Float[] temp = decibelHistory.toArray(new Float[SCREEN_VERTICAL_HEIGHT]);

        float[] dbs = new float[temp.length];
        for (int i = 0; i < SCREEN_VERTICAL_HEIGHT; ++i) {
            dbs[i] = temp[i] == null ? 0.0f : temp[i];

//            if (Math.random() > 0.5) {
//                dbs[i] *= -1;
//            }
        }

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1fv(VisualizerModel.getInstance().currentVisualizer.currentDecibelLevelHandle, dbs.length, dbs, 0);

//        GLES20.glUniform1f(VisualizerModel.getInstance().currentVisualizer.currentDecibelLevelHandle, decibelHistory.peekFirst());

    }
}