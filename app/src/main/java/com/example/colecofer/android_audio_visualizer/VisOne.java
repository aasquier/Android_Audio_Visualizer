package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;
import java.nio.FloatBuffer;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.VERTEX_AMOUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_ARRAY_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.PIXEL;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_VERTEX_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.SCREEN_VERTICAL_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

/**
 * Class VisOne
 * This class extends VisualizerBase and overrides
 * updateVertices() and draw() methods so that openGL can
 * render it's contents.
 * */

//public class VisOne extends VisualizerBase {
public class VisOne extends VisualizerBase {

    private GLLine[] lines;  //Holds the lines to be displayed
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private Utility util;
    private float[] baseLineVertices;

    /**
     * Constructor
     */
    public VisOne(Context context) {
        this.visNum = 1;
        this.lines = new GLLine[LINE_AMT];

        float k = LEFT_DRAW_BOUNDARY;

        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

        util = new Utility(context);

        this.vertexShader = util.getStringFromGLSL(R.raw.visonevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visonefragment);
    }

    /**
     * Initialization of handles during onSurfaceCreated in VisualizerRenderer
     */
    public void initOnSurfaceCreated(int positionHandle, int colorHandle) {
        this.positionHandle = positionHandle;
        this.colorHandle = colorHandle;
    }

    @Override
    public void updateVertices() {
        for(int i = 0; i < LINE_AMT; i++){
            lines[i].updateVertices();
        }
    }

    public void updateVertices(float[] newVertices) {
        for(int i = 0; i < LINE_AMT; i++){
            lines[i].updateVertices();
        }
    }

    @Override
    public void draw() {
        //Go through each line and draw them
        for(int i = 0; i < LINE_AMT; ++i) {
            lines[i].draw(this.positionHandle, this.colorHandle);
        }
    }

}
