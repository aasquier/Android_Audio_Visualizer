package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_SHIFT_FACTOR;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_WIDTH;

public class GLDot {

    private FloatBuffer dotVerticesBuffer;

    public GLDot() {

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
        dotVerticesBuffer = vertexByteBuffer.asFloatBuffer();
        dotVerticesBuffer.put(vertices).position(0);
    }

    FloatBuffer draw() {
        return this.dotVerticesBuffer;
    }
}
