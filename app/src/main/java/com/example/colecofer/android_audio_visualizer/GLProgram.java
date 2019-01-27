package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;

public class GLProgram {

    final String vertexShader =
            "uniform mat4 u_MVPMatrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec4 a_Color;" +
            "varying vec4 v_Color;" +
            "void main()" +
            "{" +
            "   v_Color = a_Color;" +
            "   gl_Position = u_MVPMatrix" +
            "               * a_Position;" +
            "}";

    final String fragmentShader =
            "precision mediump float;" +
            "varying vec4 v_Color;" +
            "void main()" +
            "{" +
            "   gl_FragColor = v_Color;" +
            "}";


    public static int loadShader(int type, String shaderCode){
        //create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        //or a fragment shader type (GLES20.GL+FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        //add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
