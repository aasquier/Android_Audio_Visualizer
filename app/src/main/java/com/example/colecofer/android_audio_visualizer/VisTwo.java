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

        // create a layer with 600 * 600 dots
        dot = new GLDot(60, 60);

        this.vertexShader =
                "uniform mat4 u_MVPMatrix;" +		        // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position;\n" + 	        // Per-vertex position information we will pass in.
                "attribute vec4 a_Color;\n" +		        // Per-vertex color information we will pass in.
                "uniform float a_DB_Level;\n" +             // The current decibel level to be used by the shader.
                "varying vec4 v_Color;\n" +                 // This will be passed into the fragment shader.
                "void main()\n" +           		        // The entry point for our vertex shader.
                "{\n" +
                "   v_Color = a_Color;\n" +	    	        // Pass the color through to the fragment shader.
                "   gl_Position = a_Position;\n" + 	        // gl_Position is a special variable used to store the final position.
                "   gl_PointSize = 30.0 * a_DB_Level;\n" +  // Will vary the pixel size from 0.25px-1.25px
                "}\n";

        this.fragmentShader =
                "precision mediump float;\n"	+	// Set the default precision to medium. We don't need as high of a
                "varying vec4 v_Color;\n" +         // This is the color from the vertex shader interpolated across the
                "void main()\n"	+	                // The entry point for our fragment shader.
                "{\n" +
                "   gl_FragColor = v_Color;\n"	+	// Pass the color directly through the pipeline.
                "}\n";
    }

    @Override
    public void updateFft(byte[] fft) {

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

        GLES20.glUniform1f(currentDbLevel, VisualizerModel.getInstance().currentVisualizer.dbHistory.peekFirst());

//        GLES10.glScalef(0.0f, 0.0f, VisualizerModel.getInstance().currentVisualizer.dbHistory.peekFirst());


        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
    }
}
