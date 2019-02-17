vec3 mod289(vec3 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
    return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v) {

    const vec4 C = vec4(0.211324865405187,
                        0.366025403784439,
                       -0.577350269189626,
                        0.024390243902439);
    vec2 i  = floor(v + dot(v, C.yy) );
    vec2 x0 = v -   i + dot(i, C.xx);
    vec2 i1;
    i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod289(i);
    vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
    + i.x + vec3(0.0, i1.x, 1.0 ));
    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m ;
    m = m*m ;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
    vec3 g;
    g.x  = a0.x  * x0.x  + h.x  * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;

    return 130.0 * dot(m, g);
}

uniform mat4   u_MVPMatrix;	        // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	        // Per-vertex position information we will pass in.
attribute vec4 a_Color;	            // Per-vertex color information we will pass in.
varying vec4   v_Color;             // This will be passed into the fragment shader.
uniform float time;                 // Time since this visualizer began
uniform float a_DB_Level[50];       // Decibel level history, need to change the 50 as the constant changes

void main() {           		    // The entry point for our vertex shader.

    //vec2 res = vec2(1.2, 1.2);

    v_Color = a_Color;              // just pass whatever input color to fragment shader, do nothing

    // ------------ wave effect begin ------------------------------------

    float noise = snoise(a_Position.xy);
    gl_Position = vec4(a_Position.x, a_Position.y + (noise * a_DB_Level[0] * 0.06), a_Position.zw);

    // -------- mirror effect begin (comment out temporary) --------------
    // making mirror
    //vec2 uv2 = a_Position.xy;
    //vec2 uv2 = a_Position.xy / res.xy;

    // horizontal mirror
    //if(uv2.y > 0.0){
    //    uv2.y = -(uv2.y - 1.04);
    //}

    //vec4 newPosition = vec4(uv2, a_Position.zw);
    //gl_Position = newPosition; 	    // gl_Position is a special variable used to store the final position.

    // -------- mirror effect end --------------
}
