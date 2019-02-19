//vec3 random3(vec3 st)
//{
//    st = vec3( dot(st,vec3(127.1,311.7,211.2)),
//            	dot(st,vec3(269.5,183.3, 157.1)), dot(st,vec3(269.5,183.3, 17.1))  );
//   	return -1.0 + 2.0*fract(sin(st)*43758.5453123);
//}
//
//float noise3D(vec3 st)
//{
//	vec3 i = floor(st) ;
//  	vec3 f = fract(st);
//
//    vec3 u = smoothstep(0.,1.,f);
//
//	float valueNowxy01 =mix( mix( dot( random3(i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ),
//                    		 	 dot( random3(i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
//                		mix( dot( random3(i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ),
//                     		 	 dot( random3(i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y);
//	float valueNowxy02 =mix( mix( dot( random3(i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ),
//                    		 	 dot( random3(i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
//                		mix( dot( random3(i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ),
//                     		 	 dot( random3(i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y);
//
//    return abs(mix(valueNowxy01, valueNowxy02, u.z));
//
//}

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
//
//float fbm(vec2 uv, float time)
//{
//    float value = 0.0;
//    float factor = 1.1;
//    float scaledTime = time / 300.0;
//
//    for (int i = 0; i < 8; i++)
//    {
//        uv += scaledTime * 0.04;
//        value += snoise(uv * factor) / factor;
//        factor *= 2.0;
//    }
//    return value;
//}
//
//float hash(float n) {
// 	return fract(cos(n*89.42)*343.42);
//}
//
//vec2 hash2(vec2 n) {
// 	return vec2(hash(n.x*23.62-300.0+n.y*34.35),hash(n.x*45.13+256.0+n.y*38.89));
//}
//
//float worley(vec2 c, float time) {
//    float dis = 1.0;
//    for(int x = -1; x <= 1; x++)
//        for(int y = -1; y <= 1; y++){
//            vec2 p = floor(c)+vec2(x,y);
//            vec2 a = hash2(p) * time;
//            vec2 rnd = 0.5+sin(a)*0.5;
//            float d = length(rnd+vec2(x,y)-fract(c));
//            dis = min(dis, d);
//        }
//    return dis;
//}
//
//float worley5(vec2 c, float time) {
//    float w = 0.0;
//    float a = 0.5;
//    for (int i = 0; i<5; i++) {
//        w += worley(c, time)*a;
//        c*=2.0;
//        time*=2.0;
//        a*=0.5;
//    }
//    return w;
//}

uniform mat4   u_MVPMatrix;	        // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	        // Per-vertex position information we will pass in.
attribute vec4 a_Color;	            // Per-vertex color information we will pass in.
varying vec4   v_Color;             // This will be passed into the fragment shader.
uniform float time;                 // Time since this visualizer began
uniform float a_DB_Level[50];       // Decibel level history, need to change the 50 as the constant changes

void main() {           		    // The entry point for our vertex shader.
    vec2 res = vec2(0.95, 0.95);

//    int positionIndex;
//    if(a_Position.y >= 0.) {
//        positionIndex = int(25. + floor(a_Position.y * 24.));
//    } else {
//        positionIndex = int(24. + floor(a_Position.y * 24.));
//    }
    float noise = snoise(vec3(a_Position.xy, a_DB_Level[0]));
//    float noise = snoise(a_Position.xy);

//    gl_Position = vec4(a_Position.x + (noise * a_DB_Level[positionIndex] * 0.025), a_Position.yzw); 	    // gl_Position is a special variable used to store the final position.
    vec4 newPosition = vec4(a_Position.x + (noise * a_DB_Level[0] * 0.02), a_Position.yzw) * 0.01; 	    // gl_Position is a special variable used to store the final position.

    gl_Position = vec4(newPosition.xy / res.xy, newPosition.zw);

    v_Color = a_Color;    	        // Pass the color through to the fragment shader.
}
