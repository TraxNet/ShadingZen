package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Vector4 {
	public float x, y, z, w;
	public Vector4(float x, float y, float z, float w){
		this.x = x; this.y = y; this.z = z; this.w = w;
	}
	public Vector4(Vector4 v){
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}
	public Vector4(Vector3 v, float w){
		x = v.getX();
		y = v.getY();
		z = v.getZ();
		this.w = w;
	}
	public Vector4(float v[]){
		x = v[0];
		y = v[1];
		z = v[2];
		w = v[3];
	}

	public void set(Vector4 v){
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}
	
	public void set(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float [] getAsArray(){
		float array[] = new float[4];
		array[0] = x;
		array[1] = y;
		array[2] = z;
		array[3] = w;
		
		return array;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public float getZ(){
		return z;
	}
	public float getW(){
		return w;
	}
	
	public Vector3 getAsVector3(){
		return new Vector3(x, y, z);
	}
	
	public float length(){
		return x * x + y * y + z * z + w * w;
	}
	
	public float lengthSqrt(){
		return  FloatMath.sqrt(length());
	}
	
	public Vector4 normalize(){
		float sqr_length =  FloatMath.sqrt(length());
		if(sqr_length >= 0.0000001f){
			float inv = 1/sqr_length;
			return new Vector4(x *inv, y *inv, z *inv, w *inv);
		}
		return new Vector4(0.f, 0.f, 0.f, 0.f);
	}
	
	public Vector4 sub(Vector4 b){
		return new Vector4(
				x - b.x,
				y - b.y,
				z - b.z,
				w - b.w
				);
	}
	public Vector4 add(Vector4 b){
		return new Vector4(
				x + b.x,
				y + b.y,
				z + b.z,
				w + b.w
				);
	}
	
	
	public static Vector4 zero(){
		return new Vector4(0.f, 0.f, 0.f, 0.f);
	}
}
