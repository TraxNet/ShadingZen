package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Vector4 {
	private float _x, _y, _z, _w;
	public Vector4(float x, float y, float z, float w){
		_x = x; _y = y; _z = z; _w = w;
	}
	public Vector4(Vector4 v){
		_x = v._x;
		_y = v._y;
		_z = v._z;
		_w = v._w;
	}
	public Vector4(Vector3 v, float w){
		_x = v.getX();
		_y = v.getY();
		_z = v.getZ();
		_w = w;
	}
	public Vector4(float v[]){
		_x = v[0];
		_y = v[1];
		_z = v[2];
		_w = v[3];
	}

	public void set(Vector4 v){
		_x = v._x;
		_y = v._y;
		_z = v._z;
		_w = v._w;
	}
	
	public void set(float x, float y, float z, float w){
		_x = x;
		_y = y;
		_z = z;
		_w = w;
	}
	
	public float [] getAsArray(){
		float array[] = new float[4];
		array[0] = _x;
		array[1] = _y;
		array[2] = _z;
		array[3] = _w;
		
		return array;
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
	public float getW(){
		return _w;
	}
	
	public Vector3 getAsVector3(){
		return new Vector3(_x, _y, _z);
	}
	
	public float length(){
		return _x*_x + _y*_y + _z*_z + _w*_w;
	}
	
	public float lengthSqrt(){
		return  FloatMath.sqrt(length());
	}
	
	public Vector4 normalize(){
		float sqr_length =  FloatMath.sqrt(length());
		if(sqr_length >= 0.0000001f){
			float inv = 1/sqr_length;
			return new Vector4(_x*inv, _y*inv, _z*inv, _w*inv);
		}
		return new Vector4(0.f, 0.f, 0.f, 0.f);
	}
	
	public Vector4 sub(Vector4 b){
		return new Vector4(
				_x - b._x,
				_y - b._y,
				_z - b._z,
				_w - b._w
				);
	}
	public Vector4 add(Vector4 b){
		return new Vector4(
				_x + b._x,
				_y + b._y,
				_z + b._z,
				_w + b._w
				);
	}
	
}
