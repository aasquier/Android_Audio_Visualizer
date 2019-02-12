package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.AMPLIFIER;
import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class GLLine {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float xOffset;
    private float rightSide;

    public GLLine(float xPosition) {
        this.xOffset = xPosition;
        this.rightSide = xOffset + PIXEL;

        //These are the default lines that are displayed before any fft values have been updated
        //TODO: This needs to generate X number of lines in the correct locations
//        this.vertices = new float[] {
//            // X, Y, Z
//            // R, G, B, A
//
//            //Bottom point
//            -1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f, 1.0f,
//
//            //Top point
//            -0.5f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f, 1.0f,
//        };

        createBaseLine();

        lineVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineVerticesBuffer.put(vertices).position(0);
    }

    /**
     * Modifies the passed in fft which is just an amplification value
     * of the current line's x-axis.
     * Converts the array into a FloatBuffer for efficiency (can be used
     * in the GPU)
     */
//    public void updateLineVertex(float[] lineVertex) {
//        int size = lineVertex.length;
//        for(int x = 0; x < size; x += 7) {
//            lineVertex[x] += (this.xOffset);
//        }
//
//        //Puts the fft array into a FloatBuffer (drawable state for the GPU)
//        FloatBuffer fftInput = ByteBuffer.allocateDirect(lineVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        fftInput.put(lineVertex).position(0);
//        this.lineVerticesBuffer = fftInput;
//    }

    public void createBaseLine(){
        this.vertices = new float[VIS1_ARRAY_SIZE];

        int vertexIndex = 0;
        float yAxis = -1.0f;
        float yOffset = (float) 2 / (VIS1_VERTEX_COUNT - 1);

        for(int i = 0; i < VIS1_ARRAY_SIZE; i+=7){
            // If left side of the line
            if(i % 2 == 0)
                this.vertices[vertexIndex] = xOffset;
                // Else right side of the line
            else
                this.vertices[vertexIndex] = xOffset + PIXEL;
            this.vertices[vertexIndex+1] = yAxis;
            this.vertices[vertexIndex+2] = 0.0f;

            // Uses retrieved color scheme to set the color
            this.vertices[vertexIndex+3] = 0.9f;
            this.vertices[vertexIndex+4] = 0.1f;
            this.vertices[vertexIndex+5] = 0.0f;
            this.vertices[vertexIndex+6] = 1.0f;

            yAxis += yOffset;
            vertexIndex+= VERTEX_AMOUNT;
        }
    }

    public void updateVertices() {
//        float[] lineVertices = new float[VIS1_ARRAY_SIZE];
        Object[] decibelArray = decibelHistory.toArray();

        int xOffset = 0;
        for(int i = 0; i < SCREEN_VERTICAL_HEIGHT; i++){
            float ampDataLeft = ((this.xOffset + (AMPLIFIER * PIXEL * (float) decibelArray[i]))) / 2;
            float ampDataRight = ((this.rightSide + (AMPLIFIER * PIXEL * (float) decibelArray[i]))) / 2;

            if(i % 2 == 1) {
                this.vertices[xOffset] = ampDataLeft;
                this.vertices[xOffset+7] = ampDataRight;
            }
            else{
                this.vertices[xOffset] = ampDataRight;
                this.vertices[xOffset+7] = ampDataLeft;
            }

            xOffset += 14;
        }

        FloatBuffer fftInput = ByteBuffer.allocateDirect(this.vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(this.vertices).position(0);
        this.lineVerticesBuffer = fftInput;
    }

    /**
     * Returns a floatbuffer of values to be drawn.
     */
    public void draw(int positionHandle, int colorHandle) {
        this.lineVerticesBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        this.lineVerticesBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, this.lineVerticesBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VIS1_VERTEX_COUNT);
    }
}