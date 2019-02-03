package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public class VisTwo extends VisualizerBase {

    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = 3;

    private final int POSITION_DATA_SIZE = 3;
    private final int COLOR_DATA_SIZE = 4;

    private final int BYTES_PER_FLOAT = 4;
    private final int STRIDE_BYTES = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT;

    private GLDot dot;

    public VisTwo(int captureSize) {
        this.captureSize = captureSize;

        // create a layer with 600 * 600 dot
        dot = new GLDot(60, 60, this.dbHistory.peekFirst());
    }

    @Override
    public void updateFft(byte[] fft) {
        dot = new GLDot(60, 60, this.dbHistory.peekFirst());
    }

    @Override
    public void updateFft(float[] fft) {

    }

    @Override
    public void draw() {
        drawDot(dot.draw(), dot.count());
    }

    private void drawDot(FloatBuffer dotVertexData, int count) {
        dotVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        dotVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
    }
}
