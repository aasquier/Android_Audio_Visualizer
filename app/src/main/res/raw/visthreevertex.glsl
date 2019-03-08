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


vec4 mod289(vec4 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 permute(vec4 x) {
     return mod289(((x*34.0)+1.0)*x);
}

vec4 taylorInvSqrt(vec4 r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}

float snoise(vec3 v)
  {
  const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;
  const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);

// First corner
  vec3 i  = floor(v + dot(v, C.yyy) );
  vec3 x0 =   v - i + dot(i, C.xxx) ;

// Other corners
  vec3 g = step(x0.yzx, x0.xyz);
  vec3 l = 1.0 - g;
  vec3 i1 = min( g.xyz, l.zxy );
  vec3 i2 = max( g.xyz, l.zxy );
  vec3 x1 = x0 - i1 + C.xxx;
  vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y
  vec3 x3 = x0 - D.yyy;      // -1.0+3.0*C.x = -0.5 = -D.y

// Permutations
  i = mod289(i);
  vec4 p = permute( permute( permute(
             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
           + i.y + vec4(0.0, i1.y, i2.y, 1.0 ))
           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));

// Gradients: 7x7 points over a square, mapped onto an octahedron.
// The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
  float n_ = 0.142857142857; // 1.0/7.0
  vec3  ns = n_ * D.wyz - D.xzx;

  vec4 j = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)

  vec4 x_ = floor(j * ns.z);
  vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)

  vec4 x = x_ *ns.x + ns.yyyy;
  vec4 y = y_ *ns.x + ns.yyyy;
  vec4 h = 1.0 - abs(x) - abs(y);

  vec4 b0 = vec4( x.xy, y.xy );
  vec4 b1 = vec4( x.zw, y.zw );

  vec4 s0 = floor(b0)*2.0 + 1.0;
  vec4 s1 = floor(b1)*2.0 + 1.0;
  vec4 sh = -step(h, vec4(0.0));

  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;

  vec3 p0 = vec3(a0.xy,h.x);
  vec3 p1 = vec3(a0.zw,h.y);
  vec3 p2 = vec3(a1.xy,h.z);
  vec3 p3 = vec3(a1.zw,h.w);

//Normalise gradients
  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
  p0 *= norm.x;
  p1 *= norm.y;
  p2 *= norm.z;
  p3 *= norm.w;

// Mix final noise value
  vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
  m = m * m;
  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1),
                                dot(p2,x2), dot(p3,x3) ) );
  }

uniform mat4   u_MVPMatrix;	        // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	        // Per-vertex position information we will pass in.
attribute vec4 a_Color;	            // Per-vertex color information we will pass in.
varying vec4   v_Color;             // This will be passed into the fragment shader.
uniform float time;                 // Time since this visualizer began
uniform float a_DB_Level[50];       // Decibel level history, need to change the 50 as the constant changes
uniform float lineFractalStrength;    // Represents how strongly the fractal effect should be applied


void main() {           		    // The entry point for our vertex shader.

    vec2 res = vec2(.8, .8);

    v_Color = a_Color;              // just pass whatever input color to fragment shader, do nothing

    int positionIndex;
    if(a_Position.y >= 0.) {
        positionIndex = int(24. + floor(a_Position.y * 24.));
    } else {
        positionIndex = int(24. + floor(a_Position.y * 24.));
    }

    float scaleTime = time / 2500.;

    float noise = snoise(vec3(a_Position.xy/res.xy, scaleTime));

    // ------------ sinus wave -------------------------------------

    vec2 uv = a_Position.xy;

    float freq = (a_DB_Level[0]+a_DB_Level[1]+a_DB_Level[2]+a_DB_Level[3]+a_DB_Level[4]+a_DB_Level[positionIndex])/6.0;

    uv.x += freq * 0.03;

    uv.y += sin(uv.x * 10.0 + scaleTime) * cos(uv.x * 3.0) * freq * 0.05;

    // ------------ wave effect ------------------------------------

//    uv = vec2(uv.x, uv.y + (noise * freq * lineFractalStrength * 0.015));

    if(lineFractalStrength == 0.) {
        uv.y += noise * 0.01;
        //uv.y += noise * lineFractalStrength/.8 * 0.01;
//        uv = vec2(uv.x, uv.y + (noise * freq * 0.05));
//    } else if(lineFractalStrength == 3) {
//        uv = vec2(uv.x, uv.y + (noise * freq * 0.03));
//    } else if(lineFractalStrength == 2) {
//        uv = vec2(uv.x, uv.y + (noise * freq * 0.02));
//    } else if(lineFractalStrength == 1){
//        uv = vec2(uv.x, uv.y + (noise * freq * 0.01));
    } else {
        uv = vec2(uv.x, uv.y + (noise * freq * lineFractalStrength * 0.015));
    }

//    float noise = snoise(vec2(a_Position.xy/res.xy));//, time/10000.));
//    if(should_Morph_To_Fractal == 3) {
//        newPosition = vec4(a_Position.x, a_Position.y + (noise * ((a_DB_Level[0]+a_DB_Level[1]+a_DB_Level[2]+a_DB_Level[3])+a_DB_Level[4]+a_DB_Level[positionIndex] / 6.0) * 0.05), a_Position.zw);
//    } else if(should_Morph_To_Fractal == 2) {
//        newPosition = vec4(a_Position.x, a_Position.y + (noise * ((a_DB_Level[0]+a_DB_Level[1]+a_DB_Level[2]+a_DB_Level[3])+a_DB_Level[4]+a_DB_Level[positionIndex] / 6.0) * 0.033), a_Position.zw);
//    } else if(should_Morph_To_Fractal == 1){
//        newPosition = vec4(a_Position.x, a_Position.y + (noise * ((a_DB_Level[0]+a_DB_Level[1]+a_DB_Level[2]+a_DB_Level[3])+a_DB_Level[4]+a_DB_Level[positionIndex] / 6.0) * 0.01625), a_Position.zw);
//    } else {
////        newPosition = vec4(a_Position.x, a_Position.y + noise * 0.05 * scaleTime, a_Position.zw);
//        newPosition = a.Position;
//    }

    // -------- scale shader --------------

    vec4 newPosition = vec4(uv/res.xy, a_Position.zw);

    // -------- apply final result --------------

    gl_Position = u_MVPMatrix * newPosition; 	    // gl_Position is a special variable used to store the final position.
}
