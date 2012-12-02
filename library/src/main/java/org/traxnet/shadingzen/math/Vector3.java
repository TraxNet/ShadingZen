package org.traxnet.shadingzen.math;

import android.util.FloatMath;

/*** Simple 3-component vector class
 * We use this to store vertex info, specify object positions, etc.
 * @author oscarblasco
 */
public class Vector3 {
	private float _x, _y, _z;
	public Vector3(){
		_x = _y = _z = 0.f;
	}
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
	
	/*** sqrt(Lenght) of this vector */
	public float lengthSqrt(){
		return FloatMath.sqrt(length());
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
	
	/***
	 * Normalizes this vector without creating a new one
	 */
	public void normalizeNoCopy(){
		float sqr_length =  FloatMath.sqrt(length());
		if(sqr_length >= 0.0000001f){
			float inv = 1/sqr_length;
			_x *= inv;
			_y *= inv;
			_z *= inv;
		} else {
			_x = 0.f;
			_y = 0.f;
			_z = 0.f;
		}
	}
	
	public float getX(){
		return _x;
	}
	public float getY(){
		return _y;
	}
	public float getZ(){
		return _z;
	}
	public Vector2 getXY(){
		return new Vector2(_x, _y);
	}
	
	public void setX(float x){
		_x = x;
	}
	public void setY(float y){
		_y = y;
	}
	public void setZ(float z){
		_z = z;
	}
	
	public void set(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public void set(Vector3 v){
		setX(v._x);
		setY(v._y);
		setZ(v._z);
	}
	
	public Vector3 sub(Vector3 b){
		return new Vector3(
				_x - b._x,
				_y - b._y,
				_z - b._z
				);
	}
	public void subNoCopy(Vector3 b){
		_x -= b._x;
		_y -= b._y;
		_z -= b._z;
	}
	
	public Vector3 add(Vector3 b){
		return new Vector3(
				_x + b._x,
				_y + b._y,
				_z + b._z
				);
	}
	
	public void addNoCopy(Vector3 b){
		_x += b._x;
		_y += b._y;
		_z += b._z;
	}
	
	public Vector3 mul(float f){
		return new Vector3(
			_x*f,
			_y*f,
			_z*f
		);
	}
	
	public Vector3 negate(){
		return new Vector3(
			-_x,
			-_y,
			-_z
			);
	}
	
}
