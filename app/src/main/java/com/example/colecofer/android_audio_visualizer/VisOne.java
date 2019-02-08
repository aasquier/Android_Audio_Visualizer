package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.channels.FileLock;
import java.util.ArrayDeque;

/**
 * Class VisOne
 * This class extends VisualizerBase and overrides
 * updateVertices() and draw() methods so that openGL can
 * render it's contents.
 * */

//public class VisOne extends VisualizerBase {
public class VisOne extends VisualizerBase {

    private final int LINE_AMT = 20;                  //Number of lines to display on the screen
    private final float AMP_MULT = 0.000005f;         //Alters the lines horizontal amplitude
    private final int VERTEX_AMOUNT = 7;              //x, y, z, r, g, b, a
    private final int BYTES_PER_FLOAT = 4;            //Amount of bytes in a float
    private final float LEFT_DRAW_BOUNDARY = -0.99f;  //Where to start drawing on the left side of the screen
    private final float RIGHT_DRAW_BOUNDARY = 0.99f;  //Right side of the screen boundary

    private final int POSITION_DATA_SIZE = 3;
    private final int STRIDE_BYTES = 7 * BYTES_PER_FLOAT;
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = 3;
    private final int COLOR_DATA_SIZE = 4;

    private int vertexCount = 5;
    private GLLine[] lines;  //Holds the lines to be displayed
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)


    /**
     * Constructor
     * @param currentVertexArraySize
     */
    public VisOne(int currentVertexArraySize) {
        this.fftArraySize = currentVertexArraySize;
        this.vertexCount = this.fftArraySize / VERTEX_AMOUNT;

        //Create 100 lines
        lines = new GLLine[LINE_AMT];
        float k = LEFT_DRAW_BOUNDARY;
        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

        this.vertexShader =
                "uniform mat4 u_MVPMatrix;" +		        // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position;\n" + 	        // Per-vertex position information we will pass in.
                "attribute vec4 a_Color;\n" +		        // Per-vertex color information we will pass in.
                "varying vec4 v_Color;\n" +                 // This will be passed into the fragment shader.
                "void main()\n" +           		        // The entry point for our vertex shader.
                "{\n" +
                "   v_Color = a_Color;\n" +	    	        // Pass the color through to the fragment shader.
                "   gl_Position = a_Position;\n" + 	        // gl_Position is a special variable used to store the final position.
                "}\n";

        this.fragmentShader =
                "precision mediump float;\n"	+	// Set the default precision to medium. We don't need as high of a
                "varying vec4 v_Color;\n" +         // This is the color from the vertex shader interpolated across the
                "void main()\n"	+	                // The entry point for our fragment shader.
                "{\n" +
                "   gl_FragColor = v_Color;\n"	+	// Pass the color directly through the pipeline.
                "}\n";

    }

    @Override
    public void updateVertices() {
        int arraySize = fftArraySize / 2;
        float[] newVerticesToRender = new float[arraySize * VERTEX_AMOUNT];

//        int j = 0;
//        float plus = (float) 1 / (arraySize / 16);
//        float k = -1.0f;
//
//        for (int i = 0; i < fftArraySize - 1; i += 2) {
////            int amplify = (fft[i]*fft[i]) + (fft[i+1]*fft[i+1]);
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
//            j+= VERTEX_AMOUNT;
//        }

        updateVertices(newVerticesToRender);
    }


    @Override
    public void updateVertices(float[] newVertices) {
        //Call updateVertices() on each line
        for (int i = 0; i < LINE_AMT; ++i) {
            float[] fftInput = new float[newVertices.length];
            System.arraycopy(newVertices, 0, fftInput, 0, newVertices.length);
            lines[i].updateFft(fftInput);
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
     * This functions will check the updated fft value and see if it's time to
     * do the highlighted pulse animation. See Leon's doc sheet for details on
     * implementation.
     */
    public void checkPulse() {

    }


    /**
     * Draw a line given a set of verticies
     * @param lineVertexData
     */
    private void drawLine(FloatBuffer lineVertexData){
        lineVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        lineVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertexCount);
    }

    /** I think this stuff is unnecessary as it is in the base class already */
//    /**
//     * Set the position handle
//     * This is necessary so that the renderer can update the position handle
//     * @param positionHandle
//     */
//    public void setPositionHandle(int positionHandle) { this.positionHandle = positionHandle; }
//
//    /**
//     * Set the color handle
//     * This is necessary so that the renderer can update the color handle
//     * @param colorHandle
//     */
//    public void setColorHandle(int colorHandle) { this.colorHandle = colorHandle; }


}
