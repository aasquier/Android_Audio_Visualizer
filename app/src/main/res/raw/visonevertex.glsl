// 3D Perlin noise helper
vec3 mod289(vec3 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

// 3D Perlin noise helper
vec4 mod289(vec4 x) {
    return x - floor(x * (1.0 / 289.0)) * 289.0;
}

// 3D Perlin noise helper
vec4 permute(vec4 x) {
     return mod289(((x*34.0)+1.0)*x);
}

// 3D Perlin noise helper
vec4 taylorInvSqrt(vec4 r) {
    return 1.79284291400159 - 0.85373472095314 * r;
}

// 3D Perlin noise function, for our purposes the third part of the 3D vector is a scaled version of the current time
float snoise(vec3 v) {
    const vec2 C = vec2(1.0/6.0, 1.0/3.0) ;
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

    // First corner
    vec3 i  = floor(v + dot(v, C.yyy) );
    vec3 x0 =   v - i + dot(i, C.xxx) ;

    // Other corners
    vec3 g  = step(x0.yzx, x0.xyz);
    vec3 l  = 1.0 - g;
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

    vec4 j   = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)

    vec4 x_  = floor(j * ns.z);
    vec4 y_  = floor(j - 7.0 * x_ );    // mod(j,N)

    vec4 x   = x_ *ns.x + ns.yyyy;
    vec4 y   = y_ *ns.x + ns.yyyy;
    vec4 h   = 1.0 - abs(x) - abs(y);

    vec4 b0  = vec4( x.xy, y.xy );
    vec4 b1  = vec4( x.zw, y.zw );

    vec4 s0  = floor(b0)*2.0 + 1.0;
    vec4 s1  = floor(b1)*2.0 + 1.0;
    vec4 sh  = -step(h, vec4(0.0));

    vec4 a0  = b0.xzyw + s0.xzyw*sh.xxyy ;
    vec4 a1  = b1.xzyw + s1.xzyw*sh.zzww ;

    vec3 p0  = vec3(a0.xy,h.x);
    vec3 p1  = vec3(a0.zw,h.y);
    vec3 p2  = vec3(a1.xy,h.z);
    vec3 p3  = vec3(a1.zw,h.w);

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
precision highp float;

void main() {           		                            // The entry point for our vertex shader.
    vec2 vertex_Field_Resolution = vec2(0.95, 0.95);        // Scales the vertex field so that the edges are not visible
    vec2 noise_Field_Resolution  = vec2(0.3, 0.3);          // Scales the noise field to adjust the noise "cell" size
    float scaled_Time            = time / 1600.0;           // Scales the system time down to control the noise evolution's speed
    vec4 new_Vertex_Position     = vec4(0.0,0.0,0.0,0.0);   // Initializes each vertices adjusted positional vector

    // Calculates a vertices noise field interaction based on the x and y positional values and the current scaled time
    float perlin_Noise_Value = snoise(vec3(a_Position.xy / noise_Field_Resolution, scaled_Time));

    //Test code to gradient color
//    float random = snoise(vec3(a_Position.z, a_DB_Level[0], (a_Position.x * a_Position.y)));
//    float reversed_db = 1.5 - a_Position.z; //a_DB_Level[0];
//    float black_scaling = reversed_db; // + random;

    // Calculates the average of the most recent six decibel levels temporaly
    float last_Six_Decibel_Readings_Average = (a_DB_Level[0] + a_DB_Level[6] + a_DB_Level[12]) / 3.0;



    /* The unused z position of a given vertex is being leveraged to pass in if it is "highlighted", the if adjusts both the x and y
        positional values to create the uneven transitions from highlighted to unhighlighted lines */
    /** HIGH */

    if(a_Position.z >= 0.39) {
        new_Vertex_Position = vec4(a_Position.x + (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), a_Position.y +
                                 (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), 0.0, a_Position.w);
//        v_Color = a_Color / 1.2;
    /** MEDIUM HIGH */
    } else if(a_Position.z >= 0.29) {
        new_Vertex_Position = vec4(a_Position.x + (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), a_Position.y +
                                 (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), 0.0, a_Position.w);
//        v_Color = a_Color / 2.0;
    /** MEDIUM */
    } else if(a_Position.z >= 0.19) {
        new_Vertex_Position = vec4(a_Position.x + (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), a_Position.y +
                                 (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), 0.0, a_Position.w);
//        v_Color = a_Color / 1.4;
    /** MEDIUM LOW */
    } else if(a_Position.z >= 0.09) {
        new_Vertex_Position = vec4(a_Position.x + (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), a_Position.y +
                                 (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), 0.0, a_Position.w);
//        v_Color = a_Color / 2.0;
    /** LOW */
    } else {
        new_Vertex_Position = vec4(a_Position.x + (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.007), a_Position.y +
                                 (perlin_Noise_Value * last_Six_Decibel_Readings_Average * 0.025), 0.0, a_Position.w);
        // Unhighlighted lines have there color value darkened towards black to help highlighted lines contrast better
//        v_Color = a_Color / 1.75;
    }

    v_Color = a_Color;// / black_scaling;

    gl_Position = vec4(new_Vertex_Position.xy / vertex_Field_Resolution, new_Vertex_Position.zw);
}
