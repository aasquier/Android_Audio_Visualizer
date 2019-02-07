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
    private final float PIXEL = 0.08f;

    private int vertexCount;
    private GLLine[] lines;  //Holds the lines to be displayed
    private float[] lineColors; //Holds the line's color value. Should be size = 4
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private float[] dbAmped; // Original straight line to come back to so that we don't accumulate width calculation and become gigantic line
    private float[] alteredDb; // Calculated version which will be passed into GLLine


    /**
     * Constructor
     * @param captureSize
     * @param colorScheme Color scheme that will be passed in from the palette analysis
     */
    public VisOne(int captureSize, float[] colorScheme) {
        this.captureSize = captureSize;
//        this.vertexCount = this.captureSize / VERTEX_AMOUNT;
        this.vertexCount = 2049;
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
        alteredDb = new float[(1024 + 1025) * VERTEX_AMOUNT];
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

    /**
     * Create the straight line consisted of triangles
     * @return
     */
    public float[] createLine(){
        // There are 1024 vertices on the right side of the line
        // and 1025 vertices on the left side of the line
        // and each vertices have 7 data
        float[] lineArrayDb = new float[(1024+1025)*VERTEX_AMOUNT];

        // Measurements to go all the way from bottom to top
        int j = 0;
        float k = -1.0f;
        float plus = (float) 2 / 2049;

        // Create the line
        for (int i = 0; i < lineArrayDb.length; i+=7) {
            // If left side of the line
            if(i % 2 == 0)
                lineArrayDb[j] = 0.0f;
            // Else right side of the line
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

    /**
     * Create an altered line vertices by increasing the width proportion via dbHistory
     * then call the renderer to pass it back
     * @param dbHistory
     */
    @Override
    public void ampByDb(ArrayDeque<Float> dbHistory) {
        // Just in case it's empty
        if(dbHistory.isEmpty()){

        }
        else{
            // Converting deque to array for traversal
            Object[] arr = dbHistory.toArray();
            // First x is the left-side of the line so get the second vertex
            int triangleTipVertex = 7;

            Log.d("GL", "----------------------------TOP DB : " + arr[1000]);

            // Get the original so that width increase doesn't get accumulated
            System.arraycopy(this.dbAmped, 0, this.alteredDb, 0, this.dbAmped.length);

            // Go through all the db data
            for(int i = 0; i < arr.length; ++i){
                // Retrieve the right side vertex
                float currentVertex = dbAmped[triangleTipVertex];
                Log.d("GL", "-----------------Vertex : " + currentVertex);

                // Increase the width by proportion
                alteredDb[triangleTipVertex] = currentVertex + (currentVertex * (float)arr[i]);

                // Next in line please
                triangleTipVertex += 14;
            }

            // Send it off to the renderer bois
            VisualizerModel.getInstance().renderer.ampByDb(alteredDb);
        }
    }

    /**
     * Sending the altered width data to individual lines
     * @param dbAmped
     */
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
