package org.traxnet.shadingzen;

import android.util.FloatMath;
import java.lang.Math;

/*
 * Basic camera class. 
 * 
 * By convention:
 * 	+y coord is UP
 *  +z coord is FORWARD, inside the screen
 *  +x coord is RIGHT 
 */
public class Camera {
	private Vector3 _position;
	
	// Variables to calculate view frustum volume
	private float _fov, _aspect, _near, _far;
	private float _yaw, _pitch, _roll;
	private Matrix4 _projectionMatrix = null;
	
	public Camera(Vector3 position, float fov, float aspect, float near, float far){
		_position = position;
		_fov = fov;
		_aspect = aspect;
		_near = near;
		_far = far;
	}
	
	public void setEulerAngles(float yaw, float pitch, float roll){
		_yaw = yaw;
		_pitch = pitch;
		_roll = roll;
	}
	
	public Vector3 getPosition(){
		return _position;
	}
	
	
	
	/***
	 * Returns the View matrix for this camera.
	 * We pass this to the shaders as an openGL SL uniform.
	 * @return View matrix for this camera.
	 */
	public Matrix4 getViewMatrix(){
		float [] matrix = new float[16];
		
		float PI = 3.1415926535898f;
		
		float sy = FloatMath.sin(_yaw+PI*0.5f);
		float cy = FloatMath.cos(_yaw+PI*0.5f);
		float sp = FloatMath.sin(_pitch-PI*0.5f);
		float cp = FloatMath.sin(_pitch-PI*0.5f);
		float sr = FloatMath.sin(_roll);
		float cr = FloatMath.sin(_roll);
		
		matrix[0] = cr*cy + sr*sp*sy;
		matrix[1] = cp*sy;
		matrix[2] = -sr*cy + cr*sp*sy;
		matrix[3] = 0.f;
		
		matrix[4] = -cr*sy + sr*sp*cy;
		matrix[5] = cp*cy;
		matrix[6] = sr*sy + cr*sp*cy;
		matrix[7] = 0.f;
		
		matrix[8] = sr*cp;
		matrix[9] = -sp;
		matrix[10] = cr*cp;
		matrix[11] = 0.f;
		
		matrix[12] = 0.f;
		matrix[13] = 0.f;
		matrix[14] = 0.f;
		matrix[15] = 1.f;
			
		return new Matrix4(matrix);
	}
	
	/***
	 * Returns the Projection matrix for this camera
	 * We pass this to the shaders as an openGL SL uniform.
	 * @return  Projection matrix for this camera.
	 */
	public Matrix4 getProjectionMatrix(){
		if(null == _projectionMatrix)
			_projectionMatrix = buildProjectionMatrix();
		
		return _projectionMatrix;
		
	}
	
	/*
	 * Builds a projection matrix with the given parameters
	 * 
	 * See Eq 3.78 at Real-Time Rendering by Tomas Moller and Eric Haines
	 */
	private Matrix4 buildProjectionMatrix(){
		
		float xmin, xmax, ymin, ymax;
		float [] frustum = new float[16];
		float doublenear, one_deltay, one_deltaz, one_deltax;
		
		xmax = _near * (float)Math.tan(_fov*0.5f);
		xmin = -xmax;
		
		ymax = xmax / _aspect;
		ymin = -ymax;
		
		doublenear = _near*2.f;
		one_deltax = 1.f / (xmax - xmin);
		one_deltay = 1.f / (ymax - ymin);
		one_deltaz = 1.f / (_far - _near);
		
		frustum[0] = doublenear * one_deltax;
		frustum[1] = 0.f;
		frustum[2] = 0.f;
		frustum[3] = 0.f;
		frustum[4] = 0.f;
		frustum[5] = doublenear * one_deltay;
		frustum[6] = 0.f;
		frustum[7] = 0.f;
		frustum[8] = (xmax + xmin) * one_deltax;
		frustum[9] = (ymax + ymin) * one_deltay;
		frustum[10] = -(_far + _near) * one_deltaz;
		frustum[11] = -1.f;
		frustum[12] = 0.f;
		frustum[13] = 0.f;
		frustum[14] = -(_far * doublenear) * one_deltaz;
		frustum[15] = 0.f;
		
		return new Matrix4(frustum);
	}
}
