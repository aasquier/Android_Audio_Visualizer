package com.example.colecofer.android_audio_visualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;

public class GLDot {

    private FloatBuffer dotVerticesBuffer;
    private int count;

    public GLDot(int width, int height) {

        this.count = width * height;

        float[] vertices = new float[count * 7];

        int index = 0;

        for(int i = 0; i < height; i++) {

            for(int j = 0; j < width; j++) {
                vertices[index*7+0] = (float)(-1.0 + 2.0 /(height + 1)*(1+i));
                vertices[index*7+1] = (float)(-1.0 + 2.0 /(width + 1)*(1+j));
                vertices[index*7+2] = 0.0f;
                vertices[index*7+3] = 1.0f;
                vertices[index*7+4] = 0.0f;
                vertices[index*7+5] = 0.0f;
                vertices[index*7+6] = 1.0f;

                index++;
            }
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        dotVerticesBuffer = vertexByteBuffer.asFloatBuffer();
        dotVerticesBuffer.put(vertices).position(0);
    }

    FloatBuffer draw() { return this.dotVerticesBuffer; }

    int count() { return this.count; }
}
