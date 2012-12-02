package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Quaternion {
	private float _x, _y, _z, _w;
	private float[] _data = null;
	
	public Quaternion(){
		setIdentity();
	}
	public Quaternion(Quaternion q){
		_x = q._x;
		_y = q._y;
		_z = q._z;
		_w = q._w;
	}
	
	public Quaternion(Vector3 v, float angle){
		setRotation(v, angle);
	}
	
	public void setIdentity(){
		_x = _y = _z = 0;
		_w = 1;
	}
	
	public Quaternion conjugate(){
		Quaternion conj = new Quaternion();
		conj._x = -_x;
		conj._y = -_y;
		conj._z = -_z;
		conj._w = _w;
		return conj;
	}
	
	/** Sets this quaternion as the angle rotation around axis v */
	public void setRotation(Vector3 v, float angle){
		float half = angle*0.5f;
		float s = FloatMath.sin(half);
		_x = v.getX()*s;
		_y = v.getY()*s;
		_z = v.getZ()*s;
		_w = FloatMath.cos(half);
	}
	
	public void invert(){
		Quaternion conj = this.conjugate();
		float length = _x*_x + _y*_y + _z*_z + _w*_w;
		length = 1.f/length;
		conj._x *= length;
		conj._y *= length;
		conj._z *= length;
		conj._w *= length;
	}
	
	/** Multiply this quaternion by another quaternion */
	public Quaternion mul(Quaternion b) {
		Quaternion ret = new Quaternion();
		ret._x = + _x*b._w + _y*b._z - _z*b._y + _w*b._x;
		ret._y = - _x*b._z + _y*b._w + _z*b._x + _w*b._y;
		ret._z = + _x*b._y - _y*b._x + _z*b._w + _w*b._z;
		ret._w = - _x*b._x - _y*b._y - _z*b._z + _w*b._w;
		return ret;
	}
	
	/**
	 * Converts a quaternion rotation operator into a matrix.
	 */
	public Matrix4 toMatrix(){
		float x2, y2, z2, xx, xy, xz, yy, yz, zz, wx, wy, wz;
		if(null == _data)
			_data = new float[16];
		// calculate coefficients
		x2 = _x + _x;
		y2 = _y + _y;
		z2 = _z + _z;

		xx = _x * x2;   xy = _x * y2;   xz = _x * z2;
		yy = _y * y2;   yz = _y * z2;   zz = _z * z2;
		wx = _w * x2;   wy = _w * y2;   wz = _w * z2;

		_data[0] = 1.0f - (yy + zz);
		_data[1] = xy - wz;
		_data[2] = xz + wy;
		_data[3] = 0.0f;
 
		_data[4] = xy + wz;
		_data[5] = 1.0f - (xx + zz);
		_data[6] = yz - wx;
		_data[7] = 0.0f;

		_data[8] = xz - wy;
		_data[9] = yz + wx;
		_data[10] = 1.0f - (xx + yy);
		_data[11] = 0.0f;

		_data[12] = 0.0f;
		_data[13] = 0.0f;
		_data[14] = 0.0f;
		_data[15] = 1.0f;
		
		return new Matrix4(_data);
	}
	
	public Quaternion set (float x, float y, float z, float w) {
        this._x = x;
        this._y = y;
        this._z = z;
        this._w = w;
        return this;
}
	
	/** Spherical linear interpolation between this quaternion and the other quaternion, based on the alpha value in the range
     * [0,1]. Taken from. Taken from Bones framework for JPCT, see http://www.aptalkarga.com/bones/
     * @param end the end quaternion
     * @param alpha alpha in the range [0,1]
     * @return this quaternion for chaining */
    public Quaternion slerp (Quaternion end, float alpha) {
            if (this.equals(end)) {
                    return this;
            }

            float result = dot(end);

            if (result < 0.0) {
                    // Negate the second quaternion and the result of the dot product
                    end.mul(-1);
                    result = -result;
            }

            // Set the first and second scale for the interpolation
            float scale0 = 1 - alpha;
            float scale1 = alpha;

            // Check if the angle between the 2 quaternions was big enough to
            // warrant such calculations
            if ((1 - result) > 0.1) {// Get the angle between the 2 quaternions,
                    // and then store the sin() of that angle
                    final double theta = Math.acos(result);
                    final double invSinTheta = 1f / Math.sin(theta);

                    // Calculate the scale for q1 and q2, according to the angle and
                    // it's sine value
                    scale0 = (float)(Math.sin((1 - alpha) * theta) * invSinTheta);
                    scale1 = (float)(Math.sin((alpha * theta)) * invSinTheta);
            }

            // Calculate the x, y, z and w values for the quaternion by using a
            // special form of linear interpolation for quaternions.
            final float x = (scale0 * this._x) + (scale1 * end._x);
            final float y = (scale0 * this._y) + (scale1 * end._y);
            final float z = (scale0 * this._z) + (scale1 * end._z);
            final float w = (scale0 * this._w) + (scale1 * end._w);
            set(x, y, z, w);

            // Return the interpolated quaternion
            return this;
    }

    public boolean equals (final Object o) {
            if (this == o) {
                    return true;
            }
            if (!(o instanceof Quaternion)) {
                    return false;
            }
            final Quaternion comp = (Quaternion)o;
            return this._x == comp._x && this._y == comp._y && this._z == comp._z && this._w == comp._w;

    }

    /** Dot product between this and the other quaternion.
     * @param other the other quaternion.
     * @return this quaternion for chaining. */
    public float dot (Quaternion other) {
            return _x * other._x + _y * other._y + _z * other._z + _w * other._w;
    }

    /** Multiplies the components of this quaternion with the given scalar.
     * @param scalar the scalar.
     * @return this quaternion for chaining. */
    public Quaternion mul (float scalar) {
    	Quaternion ret = new Quaternion();
    	ret._x = this._x * scalar;
    	ret._y = this._y * scalar;
    	ret._z = this._z * scalar;
    	ret._w = this._w * scalar;
        return ret;
    }
	
}
