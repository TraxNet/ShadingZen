#ifdef GL_FRAGMENT_PRECISION_HIGH
   precision highp float;
#else
   precision mediump float;
#endif

varying vec2 var_uv;

uniform sampler2D tex_unit;
uniform float node_alpha;
uniform vec3 node_color;

void main(void){
	vec4 tex_color = texture2D(tex_unit, var_uv);
	gl_FragColor = vec4(node_color, tex_color.r*node_alpha);
}