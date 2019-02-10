package com.example.colecofer.android_audio_visualizer;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.LINE_AMT;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS1_STRIDE_BYTES;

public class VisThree extends VisualizerBase {

    private GLLine[] lines;
    private Utility util;

    public VisThree(Context context) {

        // draw 20 lines
        lines = new GLLine[LINE_AMT];
        for (int i = 0; i < LINE_AMT; ++i) {
            float xPosition = (float)(-1.0 + 2.0 /(1 + LINE_AMT)*(1+i));
            lines[i] = new GLLine(xPosition);
        }

        util = new Utility(context);

        this.vertexShader = util.getStringFromGLSL(R.raw.visthreevertex);
        this.fragmentShader = util.getStringFromGLSL(R.raw.visthreefragment);
    }

    @Override
    public void updateVertices() {

    }

    @Override
    public void updateVertices(float[] newVertices) {

    }

    @Override
    public void draw() {
        //Go through each line and draw them
        for(int i = 0; i < LINE_AMT; ++i) {
            drawLine(lines[i].draw());
        }
    }

    private void drawLine(FloatBuffer lineVertexData){
        lineVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        lineVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS1_STRIDE_BYTES, lineVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 2);
    }
}
