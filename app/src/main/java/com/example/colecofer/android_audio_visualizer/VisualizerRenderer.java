package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_COLOR_HANDLE;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_POSITION_HANDLE;
import static com.example.colecofer.android_audio_visualizer.Constants.SHOULD_SWITCH_VIS;

public class VisualizerRenderer implements GLSurfaceView.Renderer {

    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initShaders();
    }

    /**
     * This is separated from onSurfaceCreated because it needs to be called when
     * a visualizer switches.
     * This could be refactored, because currently each visualizer will be initialized
     * when a visualizer switches which is unnecessary.
     */
    public static void initShaders() {
        /** Locals to catch the index for glsl variables */
        int positionHandle;
        int colorHandle;

        // TODO this is here in case changing the background can help us in some way for vis2
        if(VisualizerModel.getInstance().currentVisualizer instanceof VisTwo) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        } else {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }

        /* Vertex Shader Error Handling */
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            GLES20.glShaderSource(vertexShaderHandle, VisualizerModel.getInstance().currentVisualizer.getVertexShaderString());
            GLES20.glCompileShader(vertexShaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.d("Vertex Error", GLES20.glGetShaderInfoLog(vertexShaderHandle));
                GLES20.glDeleteShader(vertexShaderHandle);
                throw new RuntimeException("Could not compile vertex shader program...");
            }
        }

        /* Fragment Shader Error Handling */
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            GLES20.glShaderSource(fragmentShaderHandle, VisualizerModel.getInstance().currentVisualizer.getFragmentShaderString());
            GLES20.glCompileShader(fragmentShaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.d("Vertex Error", GLES20.glGetShaderInfoLog(fragmentShaderHandle));
                GLES20.glDeleteShader(fragmentShaderHandle);
                throw new RuntimeException("Could not compile fragment shader program...");
            }
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the shaders to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind position and color attributes
            GLES20.glBindAttribLocation(programHandle, 0, GLSL_POSITION_HANDLE);
            GLES20.glBindAttribLocation(programHandle, 1, GLSL_COLOR_HANDLE);

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] != GLES20.GL_TRUE)
            {
                GLES20.glDeleteProgram(programHandle);
                throw new RuntimeException("Could not link shader programs together...");
            }
        }

        //Get the position and color attributes
        positionHandle = GLES20.glGetAttribLocation(programHandle, GLSL_POSITION_HANDLE);
        colorHandle = GLES20.glGetAttribLocation(programHandle, GLSL_COLOR_HANDLE);

        //Initialize and handles to each specific visualizers
        VisualizerModel.getInstance().visOne.initOnSurfaceCreated(positionHandle, colorHandle, programHandle);
        VisualizerModel.getInstance().visTwo.initOnSurfaceCreated(positionHandle, colorHandle, programHandle);
        VisualizerModel.getInstance().visThree.initOnSurfaceCreated(positionHandle, colorHandle, programHandle);

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        VisualizerModel.getInstance().currentVisualizer.setSize(width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {

        if (SHOULD_SWITCH_VIS == true) {
            VisualizerModel.getInstance().checkToSwitchVisualizer();
        }

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Set the default camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0.0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the default projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        VisualizerModel.getInstance().currentVisualizer.draw(mvpMatrix);
    }
}