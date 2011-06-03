package org.traxnet.shadingzen;

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

	
	
	public float [] getAsArray(){
		float array[] = new float[4];
		array[0] = _x;
		array[1] = _y;
		array[2] = _z;
		array[3] = _w;
		
		return array;
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
	float getW(){
		return _w;
	}
}
