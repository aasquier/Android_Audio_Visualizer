package com.example.colecofer.android_audio_visualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static com.example.colecofer.android_audio_visualizer.Constants.BYTES_PER_FLOAT;

public class GLLine {

    private FloatBuffer lineVerticesBuffer;
    private float[] vertices;
    private float xOffset;

    public GLLine(float xPosition) {
        this.xOffset = xPosition;

        //These are the default lines that are displayed before any fft values have been updated
        //TODO: This needs to generate X number of lines in the correct locations
        this.vertices = new float[] {
            // X, Y, Z
            // R, G, B, A

            //Bottom point
            -1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            //Top point
            -0.5f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
        };

        lineVerticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineVerticesBuffer.put(vertices).position(0);
    }


    /**
     * Modifies the passed in fft which is just an amplification value
     * of the current line's x-axis.
     * Converts the array into a FloatBuffer for efficiency (can be used
     * in the GPU)
     * @param lineVertex
     */
    public void updateLineVertex(float[] lineVertex) {
        int size = lineVertex.length;
        for(int x = 0; x < size; x += 7) {
            lineVertex[x] += this.xOffset;
        }

        //Puts the fft array into a FloatBuffer (drawable state for the GPU)
        FloatBuffer fftInput = ByteBuffer.allocateDirect(lineVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(lineVertex).position(0);
        this.lineVerticesBuffer = fftInput;
    }

    /**
     * Returns a floatbuffer of values to be drawn.
     */
    public FloatBuffer draw() {
        return this.lineVerticesBuffer;
    }

}