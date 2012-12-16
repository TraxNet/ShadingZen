#ifdef GL_FRAGMENT_PRECISION_HIGH
   precision highp float;
#else
   precision mediump float;
#endif

uniform samplerCube tex_skydome;

varying vec3 var_position;

void main(void){
	gl_FragColor = vec4(textureCube(tex_skydome, var_position).rgb, 1.0);
}