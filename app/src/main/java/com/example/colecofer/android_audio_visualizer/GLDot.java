package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_WIDTH;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS2_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

class GLDot {

    private FloatBuffer dotVerticesBuffer;

    GLDot() {

        float[] vertices = new float[DOT_COUNT * 7];
        float COLOR_SHIFT_FACTOR_V2 = 0.005f;

        int index       = 0;
        int visTwoIndex = 1;
        int visColor    = VisualizerModel.getInstance().getColor(visTwoIndex);

        for(int i = 0; i < DOT_HEIGHT; i++) {

            for(int j = 0; j < DOT_WIDTH; j++) {
                vertices[index+0] = -1.0f + 2.0f /(DOT_WIDTH + 1)*(1+i);
                vertices[index+1] = -1.0f + 2.0f /(DOT_HEIGHT + 1)*(1+j);
                vertices[index+2] = 0.0f;
                vertices[index+3] = (Color.red(visColor) * COLOR_SHIFT_FACTOR_V2);
                vertices[index+4] = (Color.green(visColor) * COLOR_SHIFT_FACTOR_V2);
                vertices[index+5] = (Color.blue(visColor) * COLOR_SHIFT_FACTOR_V2);
                vertices[index+6] = 0.2f;

                index += 7;
            }
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        this.dotVerticesBuffer = vertexByteBuffer.asFloatBuffer();
        this.dotVerticesBuffer.put(vertices).position(0);
    }

    void draw(int positionHandle, int colorHandle, int timeHandle, int currentDecibelLevelHandle, Long visTwoStartTime) {

        /** Updates the position of individual dots for our screen rendering in the OpenGL pipeline */
        this.dotVerticesBuffer.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, this.dotVerticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        /** Updates the color information for the dots rendered to the screen in the OpenGL pipeline */
        this.dotVerticesBuffer.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, this.dotVerticesBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        Float[] temp = decibelHistory.toArray(new Float[0]);
        float[] dbs = new float[temp.length];
        for (int i = 0; i < temp.length; ++i) {
            dbs[i] = temp[i] == null ? 0.0f : temp[i];
        }

        /** Updates the size of the dots using the decibel history array */
        GLES20.glUniform1fv(currentDecibelLevelHandle, dbs.length, dbs, 0);

        GLES20.glUniform1f(timeHandle, (float)(System.currentTimeMillis() - visTwoStartTime));

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, DOT_COUNT);
    }
}
