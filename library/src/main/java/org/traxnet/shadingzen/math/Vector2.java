package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Vector2 {
	private float _x, _y;
	public Vector2(){
		_x = _y = 0.f;
	}
	public Vector2(float x, float y){
		_x = x; _y = y;
	}
	public Vector2(Vector2 v){
		_x = v._x; _y = v._y; 
	}
	public Vector2(float v[]){
		_x = v[0];
		_y = v[1];
	}
	
	public float dot(Vector2 v){
		return _x*v._x + _y*v._y;
	}
	
	/*public Vector2 cross(Vector2 v){
		return new Vector2(
			_y*v._z - _z*v._y,
			_x*v._z - _z*v._x,
			_x*v._y - _y*v._z
		);
	}*/
	
	/*** Return a float [3] 
	 * 
	 * @return an array of float with the components
	 */
	public float [] getAsArray(){
		float array[] = new float[2];
		array[0] = _x;
		array[1] = _y;
		
		return array;
	}
	
	/*** Lenght of this vector */
	public float length(){
		return _x*_x + _y*_y;
	}
	/*** Perform a normalization 
	 * If sqrt(len) of this vector is greater than an EPSILON value (0,0000001)
	 * this methods perform a normalization of this vector.
	 * Original vector is untouched, a new one is returned.
	 * @return Returns a new normalized vector.
	 */
	public Vector2 normalize(){
		float sqr_length =  FloatMath.sqrt(length());
		if(sqr_length >= 0.0000001f){
			float inv = 1/sqr_length;
			return new Vector2(_x*inv, _y*inv);
		}
		return new Vector2(0.f, 0.f);
	}
	
	public float getX(){
		return _x;
	}
	public float getY(){
		return _y;
	}

	
	public void setX(float x){
		_x = x;
	}
	public void setY(float y){
		_y = y;
	}
	
	public void set(Vector2 v){
		_x = v._x;
		_y = v._y;
	}
	
	public void set(float x, float y){
		_x = x;
		_y = y;
	}

	
	public Vector2 sub(Vector2 b){
		return new Vector2(
				_x - b._x,
				_y - b._y
				);
	}
	public Vector2 add(Vector2 b){
		return new Vector2(
				_x + b._x,
				_y + b._y
				);
	}
	
	public Vector2 mul(float f){
		return new Vector2(
			_x*f,
			_y*f
		);
	}
	
	public Vector2 negate(){
		return new Vector2(
			-_x,
			-_y
			);
	}

}
