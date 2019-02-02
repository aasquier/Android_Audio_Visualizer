package com.example.colecofer.android_audio_visualizer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLDot {

    private FloatBuffer dotVerticesBuffer;
    private float[] vertices;
    private float xOffset;

//    private final int WIDTH = 600;
//    private final int HEIGHT = 600;

//    private final int COUNT = 600 * 600;
    int COUNT = 1;

    public GLDot(float xPosition){

        // xOffset is temporary not used
        this.xOffset = xPosition;

//        this.vertices = new float[COUNT * 7];
//
//        for(int i =0; i < COUNT; i++){
//            this.vertices[(i * 7)+ 0] = 0.0f; // X
//            this.vertices[(i * 7)+ 1] = 0.0f; // Y
//            this.vertices[(i * 7)+ 2] = 0.0f; // Z
//            this.vertices[(i * 7)+ 3] = 1.0f; // R
//            this.vertices[(i * 7)+ 4] = 0.0f; // G
//            this.vertices[(i * 7)+ 5] = 0.0f; // B
//            this.vertices[(i * 7)+ 6] = 1.0f; // A
//        }
        this.vertices = new float[] {
                // X, Y, Z
                // R, G, B, A

                //Bottom point
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f
        };

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        dotVerticesBuffer = vertexByteBuffer.asFloatBuffer();
        dotVerticesBuffer.put(vertices).position(0);
    }

    public int count() { return this.COUNT; }

    public FloatBuffer draw() { return this.dotVerticesBuffer; }

}
