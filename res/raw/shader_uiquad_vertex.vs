uniform mat4 ortho_matrix;

attribute vec3 v_position;
attribute vec2 v_uv;

varying vec2 var_uv; 

void main(void){
	gl_Position = vec4(v_position,1.0)*ortho_matrix;

	var_uv = v_uv;
	//var_uv = vec2(cos(v_position.x), sin(v_position.y));
}