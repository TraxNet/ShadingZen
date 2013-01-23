package org.traxnet.shadingzen.math;

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
	
	public void setTranslation(Vector3 v){
		_m[12] += v.getX();
		_m[13] += v.getY();
		_m[14] += v.getZ();
	}
	
	/*** Multiplies a vector by this matrix
	 * 
	 * @param v Vector to apply this transformation
	 * @return
	 */
	public Vector4 mul(Vector4 v){
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
	public Vector3 mul(Vector3 v){
		float [] result = new float[4];
		float [] vec = new float[4];
		vec[0] = v.getX();
		vec[1] = v.getY();
		vec[2] = v.getZ();
		vec[3] = 1.f;
		Matrix.multiplyMV(result, 0, _m, 0, vec, 0);
		
		return new Vector3(result);
	}

    public void mul(float [] result, float [] vec){
        Matrix.multiplyMV(result, 0, _m, 0, vec, 0);
    }

	/*
	public Matrix4 mul(Matrix4 b){
		float [] result = new float[16];
		float [] mb = b.getAsArray();
		Matrix.multiplyMM(result, 0, _m, 0, mb, 0);
		
		return new Matrix4(result);
	}*/
	
	public Matrix4 inverse(){
		float [] result = new float[16];
		Matrix.invertM(result, 0, _m, 0);
		return new Matrix4(result);
	}
	
	public float [] getAsArray(){
		return _m;
	}
	
	public float [] getAsArray3x3(){
		float [] data = new float[9];
		data[0] = _m[0];
		data[1] = _m[1];
		data[2] = _m[2];
		
		data[3] = _m[4];
		data[4] = _m[5];
		data[5] = _m[6];
		
		data[6] = _m[8];
		data[7] = _m[9];
		data[8] = _m[10];
		
		return data;
	}
	
	public static Matrix4 identity(){
		Matrix4 matrix = new Matrix4();
		
		matrix._m[0] = 1.f;
		matrix._m[5] = 1.f;
		matrix._m[10] = 1.f;
		matrix._m[15] = 1.f;
		
		return matrix;
	}
	
	public void setScalingRow(float scale){
		_m[0] = scale;
		_m[5] = scale;
		_m[10] = scale;
		_m[15] = 1.f;
	}
	/*
	public static Matrix4 scale(float scale){
		Matrix4 matrix = new Matrix4();
		// TODO: Remove this method
		matrix._m[0] = scale;
		matrix._m[5] = scale;
		matrix._m[10] = scale;
		matrix._m[15] = 1.f;
		
		return matrix;
	}*/
	public void scaleByFactor(float scale) {
		_m[0] *= scale;
		_m[5] *= scale;
		_m[10] *= scale;
	}
	
	public void set(Matrix4 other){
		_m[0] = other._m[0];
		_m[1] = other._m[1];
		_m[2] = other._m[2];
		_m[3] = other._m[3];
		
		_m[4] = other._m[4];
		_m[5] = other._m[5];
		_m[6] = other._m[6];
		_m[7] = other._m[7];
		
		_m[8] = other._m[8];
		_m[9] = other._m[9];
		_m[10] = other._m[10];
		_m[11] = other._m[11];
		
		_m[12] = other._m[12];
		_m[13] = other._m[13];
		_m[14] = other._m[14];
		_m[15] = other._m[15];
	}

    public void set(float [] other){
        _m[0] = other[0];
        _m[1] = other[1];
        _m[2] = other[2];
        _m[3] = other[3];

        _m[4] = other[4];
        _m[5] = other[5];
        _m[6] = other[6];
        _m[7] = other[7];

        _m[8] = other[8];
        _m[9] = other[9];
        _m[10] = other[10];
        _m[11] = other[11];

        _m[12] = other[12];
        _m[13] = other[13];
        _m[14] = other[14];
        _m[15] = other[15];
    }
}
