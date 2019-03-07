package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;

/**
 * VisualizerBase is the fundamental abstract class that all visualizers should
 * be derived from. This way, we can have an object of this base class
 * and then assign it to derived visualizer instances, allowing us to
 * switch visualizers easily while a track is playing.
 */
abstract public class VisualizerBase {

    /** These are the "index" handles that give OpenGL a reference to refer to Javaland variables */
    protected int positionHandle;
    protected int colorHandle;
    protected int currentDecibelLevelHandle;
    protected int timeHandle;
    protected int matrixHandle;
    protected int scalingLevelArrayHandle;
    protected int deviceWidth;
    protected int deviceHeight;

    int visNum;  // A unique integer value to represent each visualizer

    protected String vertexShader;
    protected String fragmentShader;

    /**
     * Default Constructor
     */
    public VisualizerBase() {

    }

    public void setSize(int width, int height) {this.deviceWidth = width; this.deviceHeight=height; }

    String getVertexShaderString() {
        return this.vertexShader;
    }

    String getFragmentShaderString() {
        return this.fragmentShader;
    }

    /**
     * Resets the position and color handle.
     * This is useful for when switching visualizers during track playback.
     */
    public void disableVertexAttribArrays() {
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    /**
     * Called from the Renderer and should be used to update animations
     */
    abstract public void updateVertices();

    /**
     * This method will be in charge of calling the individual draw() methods
     * of other items that need to be rendered.
     */
    abstract public void draw(float[] mvpMatrix);


}
