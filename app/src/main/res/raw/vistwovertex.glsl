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

uniform mat4   u_MVPMatrix;	    // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	    // Per-vertex position information we will pass in  (a_Position.xyzw , w is always 1)
attribute vec4 a_Color;	        // Per-vertex color information we will pass in  (a_Color.rgba -->  a_Color.xyzw)
uniform float  a_DB_Level;      // The current decibel level to be used by the shader that is being passed in by each indivisual visualizer
varying vec4   v_Color;         // This will be passed into the fragment shader as the final color values

uniform float time;

void main() {
//    float nx = ((a_Position.x*a_DB_Level)) - 0.5;
//    float ny = ((a_Position.y*a_DB_Level)) - 0.5;
//    vec2 noiseVec = vec2(nx, ny);
//    vec4 newColor = vec4(a_Color.xyz, ((a_DB_Level * 2.0) * abs(snoise(a_Position.xy))));
//    vec4 newColor = vec4(a_Color.xyz, abs(snoise(noiseVec)));
//    vec4 newPosition = vec4(snoise(a_Position.xy*a_DB_Level)*2.0, snoise(a_Position.yx*a_DB_Level)*2.0, a_Position.zw);
//    vec4 newPosition = vec4(snoise(nx, ny), snoise(nx, ny), a_Position.zw);
//    v_Color = newColor;

    float scaledTime = time / 250.0;

    vec2 res = vec2(0.95, 0.95);
    vec2 cPos = vec2(2.0 * (a_Position.xy / res.xy));
    float cLength = length(cPos);
    vec2 uv = (a_Position.xy / res.xy) + (cPos / cLength) * sin(cLength * 12.0 - scaledTime * 4.0) * 0.03;
    vec4 newPosition = vec4(uv, a_Position.zw);

    v_Color = a_Color;
    gl_Position = newPosition;	        // gl_Position is a special variable used to store the final position for the fragment shader
//    gl_Position = a_Position;
    gl_PointSize = 1.0 + a_DB_Level;        // This will adjust the dot size from 1.0-2.0 based on decibel level which is in the range 0.0-1.0
}
