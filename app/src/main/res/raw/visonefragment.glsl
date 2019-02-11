precision mediump float;        // Set the default precision to high
varying vec4 v_Color;           // This is the color from the vertex shader interpolated across the

void main() {                   // The entry point for our fragment shader.
    gl_FragColor = v_Color;     // Pass the color directly through the pipeline.
}
