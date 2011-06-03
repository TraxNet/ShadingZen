uniform mat4 mvp_matrix;
uniform mat3 normal_matrix;

attribute vec3 v_position;
attribute vec3 v_normal;
attribute vec2 v_uv;

void main(void){
	gl_position = mvp_matrix*v_position;
}

