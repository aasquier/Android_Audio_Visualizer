vec3 random3(vec3 st)
{
    st = vec3( dot(st,vec3(127.1,311.7,211.2)),
            	dot(st,vec3(269.5,183.3, 157.1)), dot(st,vec3(269.5,183.3, 17.1))  );
   	return -1.0 + 2.0*fract(sin(st)*43758.5453123);
}

float noise3D(vec3 st)
{
	vec3 i = floor(st) ;
  	vec3 f = fract(st);

    vec3 u = smoothstep(0.,1.,f);

	float valueNowxy01 =mix( mix( dot( random3(i + vec3(0.0,0.0,0.0) ), f - vec3(0.0,0.0,0.0) ),
                    		 	 dot( random3(i + vec3(1.0,0.0,0.0) ), f - vec3(1.0,0.0,0.0) ), u.x),
                		mix( dot( random3(i + vec3(0.0,1.0,0.0) ), f - vec3(0.0,1.0,0.0) ),
                     		 	 dot( random3(i + vec3(1.0,1.0,0.0) ), f - vec3(1.0,1.0,0.0) ), u.x), u.y);
	float valueNowxy02 =mix( mix( dot( random3(i + vec3(0.0,0.0,1.0) ), f - vec3(0.0,0.0,1.0) ),
                    		 	 dot( random3(i + vec3(1.0,0.0,1.0) ), f - vec3(1.0,0.0,1.0) ), u.x),
                		mix( dot( random3(i + vec3(0.0,1.0,1.0) ), f - vec3(0.0,1.0,1.0) ),
                     		 	 dot( random3(i + vec3(1.0,1.0,1.0) ), f - vec3(1.0,1.0,1.0) ), u.x), u.y);

    return abs(mix(valueNowxy01, valueNowxy02, u.z));

}

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

uniform mat4   u_MVPMatrix;	        // A constant representing the combined model/view/projection matrix.
attribute vec4 a_Position;	        // Per-vertex position information we will pass in.
attribute vec4 a_Color;	            // Per-vertex color information we will pass in.
varying vec4   v_Color;             // This will be passed into the fragment shader.
uniform float time;


void main() {           		    // The entry point for our vertex shader.
    vec2 res = vec2(0.5, 0.5);
    float scaledTime = time / 1000.0;
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = a_Position.xy / res.xy;

//    // Creating the noise field
//    vec2 uv = a_Position.xy * 4.0;
//    v_Color = vec4(vec3(fbm(uv, time) * 0.5) + a_Color.xyz,1.0);

//    float _Time = iTime /*+ smoothstep(0.0, 0.25, abs(uv.x - abs(fract(iTime * 0.1)*2.-1.)))*/;

    // Time varying pixel color
    float refNoise = noise3D(vec3(uv.xy*7., scaledTime*0.8)) ;

    vec2 N=vec2(refNoise,refNoise);

    float index=0.1;
    float dotProduct=dot(N,uv);

    float constant1=1.-pow(index, 2.1)*(1.-pow(dotProduct,2.1));
    uv= index*uv  - (index*dotProduct+sqrt(constant1))*N;


    vec3 col = clamp(vec3(noise3D(vec3(uv.xy*2. + 100., scaledTime/4.))*1.,
                    noise3D(vec3(uv.xy*2. + 5220., scaledTime/4.))*1.0,
                    noise3D(vec3(uv.xy*2. + 6200., scaledTime/5.)))*2.+0.5,0.,1.);
    col.y = min(col.z, max(col.y, col.x))*col.x;


    // Output to screen
    v_Color = vec4(col,1.0);
    gl_Position = a_Position;
//    v_Color = a_Color;
}
