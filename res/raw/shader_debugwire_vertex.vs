uniform mat4 mvp_matrix;
uniform mat4 mv_matrix;
uniform mat3 normal_matrix;
uniform vec3 eye_point;
uniform vec4 color;

attribute vec3 v_position;

varying vec4 out_color;

void main(void){
	gl_Position = mvp_matrix*vec4(v_position,1.0);
	out_color = color;
}