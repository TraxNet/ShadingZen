attribute vec3 v_position;

uniform mat4 mvp_matrix;

varying vec3 var_position;

void main(void){
	gl_Position = mvp_matrix*vec4(v_position,1.0);
	var_position = normalize(v_position);
}