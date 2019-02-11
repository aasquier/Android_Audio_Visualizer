package com.example.colecofer.android_audio_visualizer;

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
    protected int currentFragmentDecibelLevelHandle;
    protected int timeHandle;

    protected int fftArraySize;
    protected String vertexShader;
    protected String fragmentShader;

    /**
     * Default Constructor
     */
    public VisualizerBase() {

    }

    String getVertexShaderString() {
        return this.vertexShader;
    }

    String getFragmentShaderString() {
        return this.fragmentShader;
    }

    /**
     * Set the position handle
     * This is necessary so that the renderer can update the position handle by giving it a reference
     * "handle": really just an index for OpenGL to use
     * @param positionHandle
     */
    public void setPositionHandle(int positionHandle) { this.positionHandle = positionHandle; }

    /**
     * Set the color handle
     * This is necessary so that the renderer can update the color handle by giving it a reference
     * "handle": really just an index for OpenGL to use
     * @param colorHandle
     */
    public void setColorHandle(int colorHandle) { this.colorHandle = colorHandle; }

    /**
     * Set the decibelLevel handle
     * This is necessary so that the renderer can update the deicbelLevel handle by giving it a reference
     * "handle": really just an index for OpenGL to use
     * @param currentDecibelLevel
     */
    public void setCurrentDecibelLevelHandle(int currentDecibelLevel) { this.currentDecibelLevelHandle = currentDecibelLevel; }


    public void setCurrentFragmentDecibelLevelHandle(int currentDecibelLevel) { this.currentFragmentDecibelLevelHandle = currentDecibelLevel; }

    public void setTimeHandle(int timeHandle) { this.timeHandle = timeHandle; }

    /**
     * Called from the Renderer and should be used to update animations
     */
    abstract public void updateVertices(byte[] newVertices);

    abstract public void updateVertices(float[] newVertices);

    /**
     * This method will be in charge of calling the individual draw() methods
     * of other items that need to be rendered.
     */
    abstract public void draw();


}
