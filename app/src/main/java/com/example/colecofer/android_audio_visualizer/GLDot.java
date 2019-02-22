package com.example.colecofer.android_audio_visualizer;

import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import static com.example.colecofer.android_audio_visualizer.Constants.DOT_COUNT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_HEIGHT;
import static com.example.colecofer.android_audio_visualizer.Constants.DOT_WIDTH;

public class GLDot {

    private FloatBuffer dotVerticesBuffer;

    public GLDot() {

        float[] vertices = new float[DOT_COUNT * 7];

        int index = 0;

        int visTwoIndex = 1;
        int visColor = VisualizerModel.getInstance().getColor(visTwoIndex);
        float shiftValue = 0.001f;

        for(int i = 0; i < DOT_HEIGHT; i++) {
            Log.d("VISCOLOR", "color: " + Integer.toString(visColor));

            for(int j = 0; j < DOT_WIDTH; j++) {
                vertices[index*7+0] = -1.0f + 2.0f /(DOT_WIDTH + 1)*(1+i);
                vertices[index*7+1] = -1.0f + 2.0f /(DOT_HEIGHT + 1)*(1+j);
                vertices[index*7+2] = 0.0f;
                vertices[index*7+3] = (Color.red(visColor) * shiftValue);
                vertices[index*7+4] = (Color.green(visColor) * shiftValue);
                vertices[index*7+5] = (Color.blue(visColor) * shiftValue);
                vertices[index*7+6] = 1.0f;

                index++;
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
