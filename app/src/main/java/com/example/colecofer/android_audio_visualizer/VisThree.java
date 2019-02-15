package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;

/**
 * Class VisThree
 * This class extends VisualizerBase and overrides
 * updateVertices() and draw() methods so that openGL can
 * render it's contents.
 * */
public class VisThree extends VisualizerBase {

    private GLLine[] lines;  //Holds the lines to be displayed
    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT_V3 - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private Utility util;

    /**
     * Constructor
     */
    public VisThree(Context context) {
        this.visNum = 3;
        this.lines = new GLLine[LINE_AMT_V3];

        float k = LEFT_DRAW_BOUNDARY;

        for(int i = 0; i < LINE_AMT_V3; ++i) {
            lines[i] = new GLLine(k);
            k += lineOffSet;
        }

        // for shader
        util = new Utility(context);

        this.vertexShader = util.getStringFromGLSL(R.raw.visthreevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visthreefragment);
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
            lines[i].draw(this.positionHandle, this.colorHandle);
        }
    }
}
