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
    for (int i = 0; i<12; i++) {
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
    float scaledTime = time / 200.0;

    for (int i = 0; i < 8; i++)
    {
        uv += scaledTime * 0.04;
        value += snoise(uv * factor) / factor;
        factor *= 2.0;
    }
    return value;
}

float radial(vec2 pos, float radius)
{
    float result = length(pos)-radius;
    result = fract(result*1.0);
    float result2 = 1.0 - result;
    float fresult = result * result2;
    fresult = pow((fresult*5.5),10.0);

    return fresult;
}

uniform mat4   u_MVPMatrix;	    // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	    // Per-vertex position information we will pass in  (a_Position.xyzw , w is always 1)
attribute vec4 a_Color;	        // Per-vertex color information we will pass in  (a_Color.rgba -->  a_Color.xyzw)
uniform float  a_DB_Level[150];  // The current decibel level to be used by the shader that is being passed in by each indivisual visualizer
varying vec4   v_Color;         // This will be passed into the fragment shader as the final color values
uniform float time;
uniform float screen_ratio;

attribute float scaling_Level;
precision highp float;          // Set the default precision to high


void main() {
    float scaledTime = time/500.0;
    vec2 res = vec2(.95, .95);
    vec2 uv = a_Position.xy / res;
    vec3 black = vec3(0.0, 0.0, 0.0);
    vec3 white = vec3(1.0, 1.0, 1.0);
    vec3 mid = vec3(0.5, 0.5, 0.5);

    vec4 newPosition = vec4(a_Position.xy/res, 0.0, 1.0);
    float d = sqrt(newPosition.x * newPosition.x + newPosition.y * newPosition.y);
    d *= 0.65;

    int distanceIndex = int(d * 75.);

    float dis = worley5(newPosition.xy/res*5., time/800.);
    vec3 b = mix(a_Color.xyz, black, dis);

    float dis2 = fbm(newPosition.xy/res*5., time);
    vec3 c = mix(a_Color.xyz, mid, dis2);

    vec4 newColor = vec4(b*c, 1.0);
    vec4 newColor2 = newColor * a_DB_Level[distanceIndex];
    vec4 newLighter = mix(newColor2, vec4(black, 1.0), 0.2);

    v_Color = mix(newColor, newLighter, .75);
    //    gl_PointSize = 1.0 + a_DB_Level[0];
    gl_PointSize = 2.0;

    if(screen_ratio > 1.) {
        newPosition.x = newPosition.x * screen_ratio;
    } else {
        newPosition.y = newPosition.y / screen_ratio;
    }

    gl_Position = newPosition;
    //    gl_position = a_Position;
}