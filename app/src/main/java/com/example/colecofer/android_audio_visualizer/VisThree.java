package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_DB_LEVEL;
import static com.example.colecofer.android_audio_visualizer.Constants.GLSL_TIME;
import static com.example.colecofer.android_audio_visualizer.Constants.LEFT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT_V3;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.RIGHT_DRAW_BOUNDARY;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class VisThree extends VisualizerBase {

    private GLLine[] lines;  //Holds the lines to be displayed
//    private float lineOffSet = (RIGHT_DRAW_BOUNDARY * 2) / (LINE_AMT_V3 - 1); //We want to display lines from -.99 to .99 (.99+.99=1.98)
    private float lineOffSet = (float)(LEFT_DRAW_BOUNDARY + 2.0 /(1 + LINE_AMT_V3));
    private Utility util;
//    private GLLineV3[] lines;
//    private Utility util;
//    private long visThreeStartTime;


    public VisThree(Context context) {
        this.visNum = 3;
        this.lines = new GLLine[LINE_AMT_V3];

//        float k = -1.0f;

        for(int i = 0; i < LINE_AMT_V3; ++i) {
            float xPosition = (float)(-1.0 + 2.0 /(1 + LINE_AMT_V3)*(1+i));
            lines[i] = new GLLine(xPosition);
//            k += lineOffSet;
        }

        // for shader
        util = new Utility(context);

        this.vertexShader = util.getStringFromGLSL(R.raw.visonevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visonefragment);
//        this.visNum = 3;
//        util = new Utility(context);
//
//        lines = new GLLineV3[LINE_NUM];
//        for (int i = 0; i < LINE_NUM; ++i) {
//            float xPosition = (float)(-1.0 + 2.0 /(1 + LINE_NUM)*(1+i));
//            lines[i] = new GLLineV3(xPosition);
//        }
//
//        this.vertexShader = util.getStringFromGLSL(R.raw.visthreevertex);
//        this.fragmentShader = util.getStringFromGLSL(R.raw.visthreefragment);
//
//        visThreeStartTime = System.currentTimeMillis();
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

    @Override
    public void draw() {
        for(int i = 0; i < LINE_AMT_V3; ++i) {
            lines[i].draw(this.positionHandle, this.colorHandle);
        }
//        for(int i = 0; i < LINE_NUM; ++i) {
//            drawLine(lines[i].draw());
//        }
    }

//    private void drawLine(FloatBuffer lineVertexData){
//        lineVertexData.position(POSITION_OFFSET);
//        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
//        GLES20.glEnableVertexAttribArray(positionHandle);
//
//        lineVertexData.position(COLOR_OFFSET);
//        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
//        GLES20.glEnableVertexAttribArray(colorHandle);
//
//        GLES20.glUniform1f(timeHandle, (float)(System.currentTimeMillis() - visThreeStartTime));
//
//
//        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);
//    }
}
