package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Vector2 {
	public float x, y;
	public Vector2(){
		x = y = 0.f;
	}
	public Vector2(float x, float y){
		this.x = x; this.y = y;
	}
	public Vector2(Vector2 v){
		x = v.x; y = v.y;
	}
	public Vector2(float v[]){
		x = v[0];
		y = v[1];
	}
	
	public float dot(Vector2 v){
		return x *v.x + y *v.y;
	}
	
	/*public Vector2 cross(Vector2 v){
		return new Vector2(
			y*v._z - _z*v.y,
			x*v._z - _z*v.x,
			x*v.y - y*v._z
		);
	}*/
	
	/*** Return a float [3] 
	 * 
	 * @return an array of float with the components
	 */
	public float [] getAsArray(){
		float array[] = new float[2];
		array[0] = x;
		array[1] = y;
		
		return array;
	}
	
	/*** Lenght of this vector */
	public float length(){
		return x * x + y * y;
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
			return new Vector2(x *inv, y *inv);
		}
		return new Vector2(0.f, 0.f);
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}

	
	public void setX(float x){
		this.x = x;
	}
	public void setY(float y){
		this.y = y;
	}
	
	public void set(Vector2 v){
		x = v.x;
		y = v.y;
	}
	
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}

	
	public Vector2 sub(Vector2 b){
		return new Vector2(
				x - b.x,
				y - b.y
				);
	}
	public Vector2 add(Vector2 b){
		return new Vector2(
				x + b.x,
				y + b.y
				);
	}
	
	public Vector2 mul(float f){
		return new Vector2(
			x *f,
			y *f
		);
	}
	
	public Vector2 negate(){
		return new Vector2(
			-x,
			-y
			);
	}

}
