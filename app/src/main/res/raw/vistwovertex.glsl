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

float hash(float n) {
 	return fract(cos(n*89.42)*343.42);
}

vec2 hash2(vec2 n) {
 	return vec2(hash(n.x*23.62-300.0+n.y*34.35),hash(n.x*45.13+256.0+n.y*38.89));
}

float worley(vec2 c, float time) {
    float dis = 1.0;
    for(int x = -1; x <= 1; x++)
        for(int y = -1; y <= 1; y++){
            vec2 p = floor(c)+vec2(x,y);
            vec2 a = hash2(p) * time;
            vec2 rnd = 0.5+sin(a)*0.5;
            float d = length(rnd+vec2(x,y)-fract(c));
            dis = min(dis, d);
        }
    return dis;
}

float worley5(vec2 c, float time) {
    float w = 0.0;
    float a = 0.5;
    for (int i = 0; i<5; i++) {
        w += worley(c, time)*a;
        c*=2.0;
        time*=2.0;
        a*=0.5;
    }
    return w;
}

float fbm(vec2 uv, float time)
{
    float value = 0.0;
    float factor = 1.1;
    float scaledTime = time / 300.0;

    for (int i = 0; i < 8; i++)
    {
        uv += scaledTime * 0.04;
        value += snoise(uv * factor) / factor;
        factor *= 2.0;
    }
    return value;
}

uniform mat4   u_MVPMatrix;	    // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	    // Per-vertex position information we will pass in  (a_Position.xyzw , w is always 1)
attribute vec4 a_Color;	        // Per-vertex color information we will pass in  (a_Color.rgba -->  a_Color.xyzw)
uniform float  a_DB_Level[50];  // The current decibel level to be used by the shader that is being passed in by each indivisual visualizer
varying vec4   v_Color;         // This will be passed into the fragment shader as the final color values
uniform float time;
attribute float scaling_Level;
precision mediump float;        // Set the default precision to high


void main() {
    float scaledTime = time / 1500.0;
    vec2 res = vec2(0.95, 0.95);

//    float dis = worley5(a_Position.xy/0.2, scaledTime);
////    vec3 b = mix(vec3(0.0, 0.0, 0.0), vec3(1.0, 1.0, 1.0), dis);
////    vec3 b = a_Color.xyz + 0.5;
//    vec3 black = vec3(0.0, 0.0, 0.0);
//    vec3 c = mix(a_Color.xyz, black, dis);
//    v_Color = vec4(c*c, 1.0);

    // Creating the noise field
//    vec2 uv = a_Position.xy * 4.0;
//    v_Color = vec4(vec3(fbm(uv, time) * 0.5) + a_Color.xyz,1.0);

//    int distanceIndex = int(sqrt(a_Position.x * a_Position.x + a_Position.y * a_Position.y)*12.);
//
//    float db = a_DB_Level[distanceIndex];
//
//    // Creating the wave itself
//    vec2 cPos = vec2(2.0 * (a_Position.xy / res.xy));
//    float cLength = length(cPos);
//    vec2 uv2 = (a_Position.xy / res.xy) + (cPos / cLength) * sin((db + a_DB_Level[0]) * cLength * 24.0 - scaledTime) * 0.02;
//    vec4 newPosition = vec4(uv2, a_Position.zw);
//
//    // Feeding the position to the fragment shader
//    gl_Position = newPosition;
    gl_PointSize = 30.0 * a_DB_Level[1];
    v_Color = a_Color;
    gl_Position = a_Position;
}
