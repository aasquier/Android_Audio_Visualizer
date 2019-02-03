package com.example.colecofer.android_audio_visualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLDot {

    private FloatBuffer dotVerticesBuffer;

    private int COUNT;

    public GLDot(int width, int height) {

        this.COUNT = width * height;

        float[] vertices = new float[COUNT * 7];

        int count = 0;

        for(int i = 0; i < height; i++) {

            for(int j = 0; j < width; j++) {
                vertices[count*7+0] = (float)(-1.0 + 2.0 /(height + 1)*(1+i));
                vertices[count*7+1] = (float)(-1.0 + 2.0 /(width + 1)*(1+j));
                vertices[count*7+2] = 0.0f;
                vertices[count*7+3] = 1.0f;
                vertices[count*7+4] = 0.0f;
                vertices[count*7+5] = 0.0f;
                vertices[count*7+6] = 1.0f;

                count++;
            }
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        dotVerticesBuffer = vertexByteBuffer.asFloatBuffer();
        dotVerticesBuffer.put(vertices).position(0);
    }

    public FloatBuffer draw() { return this.dotVerticesBuffer; }

    public int count() {return this.COUNT; }
}
