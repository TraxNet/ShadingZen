#ifdef GL_FRAGMENT_PRECISION_HIGH
   precision highp float;
#else
   precision mediump float;
#endif

varying vec2 var_uv;

uniform sampler2D tex_unit;

void main(void){
	gl_FragColor = vec4(texture2D(tex_unit, var_uv).rgb, 1.0);
}