package com.example.colecofer.android_audio_visualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Class VisOne
 * This class implements the GLVisualizer interface which forces it to
 * implement the appropriate openGL and fft methods so that it can be rendered.
 * */

public class VisOne implements GLVisualizer {

    private final float AMP_MULT = 0.000005f;  //Alters the lines horizontal amplitude
    private final int VERTEX_AMOUNT = 7;       //x, y, z, r, g, b, a
    private final int BYTES_PER_FLOAT = 4;     //Amount of bytes in a float


    private int captureSize;
    private int vertexCount = 5;

    private float[] lineVertices;
    private FloatBuffer lineVertexBuffer;
    private GLLine[] lines;                               //Holds the lines to be displayed
    private final int LINE_AMT = 20;                      //Number of lines to display on the screen
    private float lineOffSet = 1.98f/(LINE_AMT -1);       //We want to display lines from -.99 to .99 (.99+.99=1.98)


    /**
     * Constructor
     * @param captureSize
     */
    public VisOne(int captureSize) {
        this.captureSize = captureSize;
        this.vertexCount = this.captureSize / VERTEX_AMOUNT;

//        //These are the default lines that are displayed before any fft values have been updated
//        //TODO: This needs to generate X number of lines in the correct locations
//        this.lineVertices = new float[] {
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
//
//        };

//        lineVertexBuffer = ByteBuffer.allocateDirect(lineVertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        lineVertexBuffer.put(lineVertices).position(0);

        //Create 100 lines
        lines = new GLLine[LINE_AMT];
        float k = -0.99f;
        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

    }


    @Override
    public void updateFft(float[] fft) {

        //Call updateFft() on each line
        for (int i = 0; i < LINE_AMT; ++i) {
            float[] fftInput = new float[fft.length];
            System.arraycopy(fft, 0, fftInput, 0, fft.length);
            lines[i].updateFft(fftInput);
        }

//        int arraySize = captureSize / 2;
//        float[] fftRender = new float[arraySize * VERTEX_AMOUNT];
//
//        int j = 0;
//        float plus = (float) 1 / (arraySize / 16);
//        float k = -1.0f;
//
//        for (int i = 0; i < captureSize-1; i += 2) {
//            int amplify = (fft[i]*fft[i]) + (fft[i+1]*fft[i+1]);
//
//            fftRender[j] = (float)amplify * AMP_MULT;
//            fftRender[j+1] = k;
//            fftRender[j+2] = 0.0f;
//            fftRender[j+3] = 1.0f;
//            fftRender[j+4] = 0.0f;
//            fftRender[j+5] = 0.0f;
//            fftRender[j+6] = 1.0f;
//
//            k += plus;
//            //i++;
//            j+= VERTEX_AMOUNT;
//        }
    }


    @Override
    public void draw(int program) {

    }


    /**
     * This functions will check the updated fft value and see if it's time to
     * do the highlighted pulse animation. See Leon's doc sheet for details on
     * implementation.
     */
    public void checkPulse() {

    }
}
