uniform mat4 mvp_matrix;
uniform mat4 mv_matrix;
uniform vec3 light_pos;
uniform vec4 diffuse_color;
uniform vec4 ambient_color;

attribute vec3 v_position;
attribute vec3 v_normal;
attribute vec2 v_uv;

varying float var_LightIntensity;
varying vec3 var_normal;  
varying vec3 var_light;  
varying vec3 var_eyedir; 
varying float model_depth; 
varying vec2 var_uv; 

void main(void){
	gl_Position = mvp_matrix*vec4(v_position,1.0);
	var_light = vec3(0.0,-0.707,0.707);
	var_eyedir = normalize(v_position-light_pos);
	var_normal = v_normal;
	var_LightIntensity = 1.0;
	model_depth = sqrt(length(v_position));
	var_uv = v_uv;
}