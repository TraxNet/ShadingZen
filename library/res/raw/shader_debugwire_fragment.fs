#ifdef GL_FRAGMENT_PRECISION_HIGH
   precision highp float;
#else
   precision mediump float;
#endif

const vec3 c_Color = vec3(1.0,1.0,1.0);
const vec3 c_Color2 = vec3(0.2,0.2,0.3);
const vec3 c_Color3 = vec3(0.0,0.0,0.4);

varying vec4 out_color;


void main(void){
	gl_FragColor = out_color;
}