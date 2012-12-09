attribute vec3 v_position;

uniform mat4 p_matrix;
uniform mat4 mv_matrix;

varying vec3 var_position; 

void main(void){
	gl_Position = p_matrix*mv_matrix*vec4(v_position*1500.0,1.0);
	var_position = v_position;
}