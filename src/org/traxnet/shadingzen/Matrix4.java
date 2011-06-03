package org.traxnet.shadingzen;

import android.opengl.Matrix;

/*
 * Simple Matrix4x4 implementation
 * 
 * The values are stored in column-major order (as in OpenGL)
 */
public class Matrix4 {
	float _m[];
	
	public Matrix4(){
		_m = new float[16];
	}
	public Matrix4(float [] m){
		_m = m;
	}
	
	/*** Multiplies a vector by this matrix
	 * 
	 * @param v Vector to apply this transformation
	 * @return
	 */
	public Vector4 Mul(Vector4 v){
		float [] result = new float[4];
		float [] vec = v.getAsArray();
		Matrix.multiplyMV(result, 0, _m, 0, vec, 0);
		
		return new Vector4(result);
	}
	/*** Multiplies a 3-component vector against this matrix
	 * Vector is packed into a Vec4(x,y,z,0) before multiply
	 * @param v Vector to transform
	 * @return
	 */
	public Vector3 Mul(Vector3 v){
		float [] result = new float[4];
		float [] vec = new float[4];
		vec[0] = v.getX();
		vec[1] = v.getY();
		vec[2] = v.getZ();
		vec[3] = 0.f;
		Matrix.multiplyMV(result, 0, _m, 0, vec, 0);
		
		return new Vector3(result);
	}
	/*** Multiply two 4x4 matrices
	 * Data is converted back to a pack of float and multiplied
	 * using the Android helper functions (which I assume are optimized...)
	 * @param b Right side matrix in this operation
	 * @return A new matrix (this*b)
	 */
	public Matrix4 Mul(Matrix4 b){
		float [] result = new float[16];
		float [] mb = b.getAsArray();
		Matrix.multiplyMM(result, 0, _m, 0, mb, 0);
		
		return new Matrix4(result);
	}
	
	public float [] getAsArray(){
		return _m;
	}
}
