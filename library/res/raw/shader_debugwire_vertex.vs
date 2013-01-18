//uniform vec3 position;
uniform vec4 color;
uniform mat4 projection;

attribute vec3 v_position;

varying vec4 out_color;

void main(void){
	gl_Position = projection*vec4(v_position,1.0);
	out_color = color;
}