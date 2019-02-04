package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;

/**
 * Class VisOne
 * This class extends VisualizerBase and overrides
 * updateFft() and draw() methods so that openGL can
 * render it's contents.
 * */

//public class VisOne extends VisualizerBase {
public class VisOne extends VisualizerBase {

    private final int LINE_AMT = 10;                  //Number of lines to display on the screen
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
    private final float PIXEL = 0.002f;

    private int vertexCount = 2049;
    private GLLine[] lines;  //Holds the lines to be displayed
    private float[] lineColors; //Holds the line's color value. Should be size = 4
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private float[] dbAmped;


    /**
     * Constructor
     * @param captureSize
     * @param colorScheme Color scheme that will be passed in from the palette analysis
     */
    public VisOne(int captureSize, float[] colorScheme) {
        this.captureSize = captureSize;
        this.vertexCount = this.captureSize / VERTEX_AMOUNT;
        this.lineColors = new float[4];

        //Create 100 lines
        lines = new GLLine[LINE_AMT];
        float k = LEFT_DRAW_BOUNDARY;
        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

        // If colorScheme input is correct then copy it
        // Else use default which is just red
        if(colorScheme != null && colorScheme.length == 4)
            System.arraycopy(colorScheme, 0, this.lineColors, 0, colorScheme.length);
        else {
            this.lineColors[0] = 1.0f;
            this.lineColors[1] = 0.0f;
            this.lineColors[2] = 0.0f;
            this.lineColors[3] = 1.0f;
        }

        dbAmped = createLine();
    }

    @Override
    public void updateFft(byte[] fft) {
        int arraySize = captureSize / 2;
        float[] fftRender = new float[arraySize * VERTEX_AMOUNT];

        int j = 0;
        float plus = (float) 1 / (arraySize / 16);
        float k = -1.0f;

        for (int i = 0; i < captureSize - 1; i += 2) {
            int amplify = (fft[i]*fft[i]) + (fft[i+1]*fft[i+1]);

            fftRender[j] = (float)amplify * AMP_MULT;
            fftRender[j+1] = k;
            fftRender[j+2] = 0.0f;

            // Uses retrieved color scheme to set the color
            fftRender[j+3] = lineColors[0];
            fftRender[j+4] = lineColors[1];
            fftRender[j+5] = lineColors[2];
            fftRender[j+6] = lineColors[3];

            k += plus;
            j+= VERTEX_AMOUNT;
        }

        VisualizerModel.getInstance().renderer.updateFft(fftRender);
    }


    @Override
    public void updateFft(float[] fft) {
        //Call updateFft() on each line
        for (int i = 0; i < LINE_AMT; ++i) {
            float[] fftInput = new float[fft.length];
            System.arraycopy(fft, 0, fftInput, 0, fft.length);
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

    public float[] createLine(){
        float[] lineArrayDb = new float[(1024+1025)*7];

        int j = 0;
        float k = -1.0f;
        float plus = (float) 2 / 2049;

        for (int i = 0; i < lineArrayDb.length; i+=7) {
            if(i % 2 == 1)
                lineArrayDb[j] = 0.0f;
            else
                lineArrayDb[j] = 0.0f + PIXEL;
            lineArrayDb[j+1] = k;
            lineArrayDb[j+2] = 0.0f;

            // Uses retrieved color scheme to set the color
            lineArrayDb[j+3] = lineColors[0];
            lineArrayDb[j+4] = lineColors[1];
            lineArrayDb[j+5] = lineColors[2];
            lineArrayDb[j+6] = lineColors[3];

            k += plus;
            j+= VERTEX_AMOUNT;
        }

        return lineArrayDb;
    }

    @Override
    public void ampByDb(ArrayDeque<Float> dbHistory) {
        // Just in case it's empty
        if(dbHistory.isEmpty()){

        }
        else{
            // Converting deque to array for traversal
            Object[] arr = dbHistory.toArray();
            int triangleTipVertex = 1;

            for(int i = 0; i < arr.length; ++i){
                float currentVertex = dbAmped[triangleTipVertex];
                dbAmped[triangleTipVertex] = currentVertex + (currentVertex * (float)arr[i]);
                triangleTipVertex += 2;
            }
        }

        VisualizerModel.getInstance().currentVisualizer.ampByDb(dbAmped);
    }

    public void ampByDb(float[] dbAmped) {
        for (int i = 0; i < LINE_AMT; ++i) {
            lines[i].ampByDb(dbAmped);
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
    }

    /**
     * Set the position handle
     * This is necessary so that the renderer can update the position handle
     * @param positionHandle
     */
    public void setPositionHandle(int positionHandle) { this.positionHandle = positionHandle; }

    /**
     * Set the color handle
     * This is necessary so that the renderer can update the color handle
     * @param colorHandle
     */
    public void setColorHandle(int colorHandle) { this.colorHandle = colorHandle; }


}
