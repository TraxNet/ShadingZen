uniform mat4 mvp_matrix;

attribute vec2 v_position;
attribute vec2 v_uv;

varying vec2 var_uv; 

void main(void){
	gl_Position = mvp_matrix*vec4(v_position, 0.0, 1.0);

	var_uv = v_uv;
}