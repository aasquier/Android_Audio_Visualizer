// TODO: consider delete it if we don't need it anymore, we got GLDot for VisTwo
//package com.example.colecofer.android_audio_visualizer;
//
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//
//import static com.example.colecofer.android_audio_visualizer.Constants.COUNT;
//
//public class GLCircle {
//
//    private FloatBuffer circleVerticesBuffer;
//    private float[] vertices;
//
//    public GLCircle(float xPosition){
//
//        this.vertices = new float[COUNT * 7];
//
//        for(int i =1; i < COUNT; i++){
//            this.vertices[(i * 7)+ 0] = (float) (0.5 * Math.cos((3.14/180) * (float)i )); // X
//            this.vertices[(i * 7)+ 1] = (float) (0.5 * Math.sin((3.14/180) * (float)i )); // Y
//            this.vertices[(i * 7)+ 2] = 0; // Z
//            this.vertices[(i * 7)+ 3] = 1.0f; // R
//            this.vertices[(i * 7)+ 4] = 0.0f; // G
//            this.vertices[(i * 7)+ 5] = 0.0f; // B
//            this.vertices[(i * 7)+ 6] = 1.0f; // A
//        }
//
//        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
//        vertexByteBuffer.order(ByteOrder.nativeOrder());
//        circleVerticesBuffer = vertexByteBuffer.asFloatBuffer();
//        circleVerticesBuffer.put(vertices).position(0);
//    }
//
//    public FloatBuffer draw() { return this.circleVerticesBuffer; }
//
//}
