package org.traxnet.shadingzen.math;

import android.util.FloatMath;

/*** Simple 3-component vector class
 * We use this to store vertex info, specify object positions, etc.
 * @author oscarblasco
 */
public class Vector3 {
	public float x, y, z;
	public Vector3(){
		x = y = z = 0.f;
	}
	public Vector3(float x, float y, float z){
		this.x = x; this.y = y; this.z = z;
	}
	public Vector3(Vector3 v){
		x = v.x; y = v.y; z = v.z;
	}
	public Vector3(float v[]){
		x = v[0];
		y = v[1];
		z = v[2];
	}
	
	public float dot(Vector3 v){
		return x *v.x + y *v.y + z *v.z;
	}
	
	public Vector3 cross(Vector3 v){
		return new Vector3(
			y *v.z - z *v.y,
			x *v.z - z *v.x,
			x *v.y - y *v.z
		);
	}
	
	/*** Return a float [3] 
	 * 
	 * @return an array of float with the components
	 */
	public float [] getAsArray(){
		float array[] = new float[3];
		array[0] = x;
		array[1] = y;
		array[2] = z;
		
		return array;
	}
	
	/*** Lenght of this vector */
	public float length(){
		return x * x + y * y + z * z;
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
			return new Vector3(x *inv, y *inv, z *inv);
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
			x *= inv;
			y *= inv;
			z *= inv;
		} else {
			x = 0.f;
			y = 0.f;
			z = 0.f;
		}
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
	public Vector2 getXY(){
		return new Vector2(x, y);
	}
	
	public void setX(float x){
		this.x = x;
	}
	public void setY(float y){
		this.y = y;
	}
	public void setZ(float z){
		this.z = z;
	}
	
	public void set(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public void set(Vector3 v){
		setX(v.x);
		setY(v.y);
		setZ(v.z);
	}
	
	public Vector3 sub(Vector3 b){
		return new Vector3(
				x - b.x,
				y - b.y,
				z - b.z
				);
	}
	public void subNoCopy(Vector3 b){
		x -= b.x;
		y -= b.y;
		z -= b.z;
	}
	
	public Vector3 add(Vector3 b){
		return new Vector3(
				x + b.x,
				y + b.y,
				z + b.z
				);
	}
	
	public void addNoCopy(Vector3 b){
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public Vector3 mul(float f){
		return new Vector3(
			x *f,
			y *f,
			z *f
		);
	}
	
	public Vector3 negate(){
		return new Vector3(
			-x,
			-y,
			-z
			);
	}
	
}
