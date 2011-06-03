package org.traxnet.shadingzen;

import android.util.FloatMath;

/*** Simple 3-component vector class
 * We use this to store vertex info, specify object positions, etc.
 * @author oscarblasco
 */
public class Vector3 {
	private float _x, _y, _z;
	public Vector3(float x, float y, float z){
		_x = x; _y = y; _z = z;
	}
	public Vector3(Vector3 v){
		_x = v._x; _y = v._y; _z = v._z;
	}
	public Vector3(float v[]){
		_x = v[0];
		_y = v[1];
		_z = v[2];
	}
	
	public float dot(Vector3 v){
		return _x*v._x + _y*v._y + _z*v._z;
	}
	
	public Vector3 cross(Vector3 v){
		return new Vector3(
			_y*v._z - _z*v._y,
			_x*v._z - _z*v._x,
			_x*v._y - _y*v._z
		);
	}
	
	/*** Return a float [3] 
	 * 
	 * @return an array of float with the components
	 */
	public float [] getAsArray(){
		float array[] = new float[3];
		array[0] = _x;
		array[1] = _y;
		array[2] = _z;
		
		return array;
	}
	
	/*** Lenght of this vector */
	public float length(){
		return _x*_x + _y*_y + _z*_z;
	}
	/*** Perform a normalization 
	 * If sqrt(len) of this vector is greater than an EPSILON value (0,0000001)
	 * this methods perform a normalization of this vector.
	 * Original vector is untouched, a new one is returned.
	 * @return Returns a new normalized vector.
	 */
	public Vector3 normalize(){
		float sqr_length =  FloatMath.sqrt(length());
		if(sqr_length >= 0.0000001f){
			float inv = 1/sqr_length;
			return new Vector3(_x*inv, _y*inv, _z*inv);
		}
		return new Vector3(0.f, 0.f, 0.f);
	}
	
	float getX(){
		return _x;
	}
	float getY(){
		return _y;
	}
	float getZ(){
		return _z;
	}
	
}
