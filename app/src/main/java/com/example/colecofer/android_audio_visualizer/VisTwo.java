package com.example.colecofer.android_audio_visualizer;

import android.opengl.GLES20;
import java.nio.FloatBuffer;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.COLOR_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_DATA_SIZE;
import static com.example.colecofer.android_audio_visualizer.Constants.POSITION_OFFSET;
import static com.example.colecofer.android_audio_visualizer.Constants.VIS2_STRIDE_BYTES;
import static com.example.colecofer.android_audio_visualizer.VisualizerActivity.decibelHistory;

public class VisTwo extends VisualizerBase {

    private GLDot dot;

    public VisTwo() {
        // create a layer with 600 * 600 dots
        dot = new GLDot(1500, 1500);

        this.vertexShader =
                "vec3 mod289(vec3 x) {\n" +
                    "return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
                "}\n" +

                "vec2 mod289(vec2 x) {\n" +
                    "return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
                "}\n" +

                "vec3 permute(vec3 x) {\n" +
                    "return mod289(((x*34.0)+1.0)*x);\n" +
                "}\n" +
//
                "float snoise(vec2 v)\n" +
                "{\n" +
                    "const vec4 C = vec4(0.211324865405187,\n" +
                    "0.366025403784439,\n" +
                    "-0.577350269189626,\n" +
                    "0.024390243902439);\n" +
                    "vec2 i  = floor(v + dot(v, C.yy) );\n" +
                    "vec2 x0 = v -   i + dot(i, C.xx);\n" +
                    "vec2 i1;\n" +
                    "i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);\n" +
                    "vec4 x12 = x0.xyxy + C.xxzz;\n" +
                    "x12.xy -= i1;\n" +
                    "i = mod289(i);\n" +
                    "vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))\n" +
                    "+ i.x + vec3(0.0, i1.x, 1.0 ));\n" +
                    "vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);\n" +
                    "m = m*m ;\n" +
                    "m = m*m ;\n" +
                    "vec3 x = 2.0 * fract(p * C.www) - 1.0;\n" +
                    "vec3 h = abs(x) - 0.5;\n" +
                    "vec3 ox = floor(x + 0.5);\n" +
                    "vec3 a0 = x - ox;\n" +
                    "m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );\n" +
                    "vec3 g;\n" +
                    "g.x  = a0.x  * x0.x  + h.x  * x0.y;\n" +
                    "g.yz = a0.yz * x12.xz + h.yz * x12.yw;\n" +
                    "return 130.0 * dot(m, g);\n" +
                "}\n" +

                "uniform mat4 u_MVPMatrix;" +		        // A constant representing the combined model/view/projection matrix.
                "attribute vec4 a_Position;\n" + 	        // Per-vertex position information we will pass in.
                "attribute vec4 a_Color;\n" +		        // Per-vertex color information we will pass in.
                "uniform float a_DB_Level;\n" +             // The current decibel level to be used by the shader.
                "varying vec4 v_Color;\n" +                 // This will be passed into the fragment shader.

                "void main()\n" +           		        // The entry point for our vertex shader.
                "{\n" +
                "   v_Color = a_Color;\n" +	    	        // Pass the color through to the fragment shader.
//                "   vec4 newPosition = vec4(snoise(a_Position.xy*a_DB_Level)*2.0," +
//                "                           snoise(a_Position.yx*a_DB_Level)*2.0, a_Position.zw);\n" +
//                "   gl_Position = newPosition;\n" + 	        // gl_Position is a special variable used to store the final position.
                "   gl_Position = a_Position;\n" +
                "   gl_PointSize = 1.0 + a_DB_Level;\n" +  // Will vary the pixel size from 0.25px-1.25px
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
    public void updateVertices() {

    }

    @Override
    public void updateVertices(float[] newVertices) {

    }

    // TODO We may want to consider moving the "drawDot" logic into this function, it seems to be serving no real purpose
    @Override
    public void draw() {
        drawDot(dot.draw(), dot.count());
    }

    private void drawDot(FloatBuffer dotVertexData, int count) {
        /** Updates the position of individual dots for our screen rendering in the OpenGL pipeline */
        dotVertexData.position(POSITION_OFFSET);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(positionHandle);

        /** Updates the color information for the dots rendered to the screen in the OpenGL pipeline */
        dotVertexData.position(COLOR_OFFSET);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, VIS2_STRIDE_BYTES, dotVertexData);
        GLES20.glEnableVertexAttribArray(colorHandle);

        /** Updates the size of the dots using the most current decibel level, i.e. the first element of the decibel history */
        GLES20.glUniform1f(currentDecibelLevelHandle, decibelHistory.peekFirst());

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
    }
}
