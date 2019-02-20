package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;

import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_TIME;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;

/**
 * Class VisThree
 * This class extends VisualizerBase and overrides
 * updateVertices() and draw() methods so that openGL can
 * render it's contents.
 * */
public class VisThree extends VisualizerBase {

    private GLLineV3[] lines;  //Holds the lines to be displayed
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2 + 0.014f) / (LINE_AMT_V3 - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private Utility util;

    private long visThreeStartTime;

    /**
     * Constructor
     */
    public VisThree(Context context) {
        this.visNum = 3;
        this.lines = new GLLineV3[LINE_AMT_V3];

        float k = LEFT_DRAW_BOUNDARY;

        for(int i = 0; i < LINE_AMT_V3; ++i) {
            lines[i] = new GLLineV3(k);
            k += lineOffSet;
        }

        // for shader
        util = new Utility(context);

        this.vertexShader = util.getStringFromGLSL(R.raw.visthreevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visthreefragment);

        this.visThreeStartTime = System.currentTimeMillis();
    }

    /**
     * Initialization of handles during onSurfaceCreated in VisualizerRenderer
     */
    public void initOnSurfaceCreated(int positionHandle, int colorHandle, int programHandle) {
        this.positionHandle = positionHandle;
        this.colorHandle = colorHandle;
        this.currentDecibelLevelHandle = GLES20.glGetUniformLocation(programHandle, GLSL_DB_LEVEL);
        this.timeHandle = GLES20.glGetUniformLocation(programHandle, GLSL_TIME);
    }

    @Override
    public void updateVertices() {
        for(int i = 0; i < LINE_AMT_V3; i++){
            lines[i].updateVertices();
        }
    }

    /**
     * Calls line's draw call
     */
    @Override
    public void draw() {
        //Go through each line and draw them
        for(int i = 0; i < LINE_AMT_V3; ++i) {
            lines[i].draw(this.positionHandle, this.colorHandle, this.timeHandle, this.visThreeStartTime);
        }
    }
}
