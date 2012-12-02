#ifdef GL_FRAGMENT_PRECISION_HIGH
   precision highp float;
#else
   precision mediump float;
#endif

uniform vec4 diffuse_color;
uniform vec4 ambient_color;

const vec3 c_Color = vec3(1.0,1.0,1.0);
const vec3 c_Color2 = vec3(0.2,0.2,0.3);
const vec3 c_Color3 = vec3(0.0,0.0,0.4);

varying float var_LightIntensity; 
varying vec3 var_normal;  
varying vec3 var_light; 
varying vec3 var_eyedir; 
varying float model_depth; 
varying vec2 var_uv;

uniform sampler2D tex_unit;

void main(void){
	// Just multiply the interpolated intesity with the color
	// We are not computing the light eq. per pixel
	float depth_interpolation = exp(model_depth/1.2);
	float intensity = dot(var_normal,var_light);
	float intensity2 = dot(var_normal,var_eyedir);
	vec3 _rgb = c_Color*depth_interpolation+c_Color2*(1.0-depth_interpolation);
	
	//gl_FragColor = vec4(_rgb*max(0.2,intensity)+c_Color3*(1.0-intensity2), 1.0);
	//gl_FragColor = vec4(0.05, 0.0, 0.2, 1.0)+texture2D(tex_unit, var_uv);
	//gl_FragColor = diffuse_color*intensity+vec4(ambient_color.rgb*pow(10.0,(1.0-intensity2)*0.5), 1.0);
	gl_FragColor = max(0.1,intensity)*texture2D(tex_unit, var_uv)+vec4(ambient_color.rgb*pow(6.0,(1.0-intensity2)*1.2)*0.3, 1.0);
}