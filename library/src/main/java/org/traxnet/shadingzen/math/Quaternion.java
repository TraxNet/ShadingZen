package org.traxnet.shadingzen.math;

import android.util.FloatMath;

public class Quaternion {
	public float x, y, z, w;
	public float[] _data = null;
	
	public Quaternion(){
		setIdentity();
	}
	public Quaternion(Quaternion q){
		x = q.x;
		y = q.y;
		z = q.z;
		w = q.w;
	}
	
	public Quaternion(Vector3 v, float angle){
		setRotation(v, angle);
	}

    public Quaternion(float x, float y, float z, float angle){
        setRotation(x, y, z, angle);
    }
	
	public void setIdentity(){
		x = y = z = 0;
		w = 1;
	}
	
	public Quaternion conjugate(){
		Quaternion conj = new Quaternion();
		conj.x = -x;
		conj.y = -y;
		conj.z = -z;
		conj.w = w;
		return conj;
	}
	
	/** Sets this quaternion as the angle rotation around axis v */
	public void setRotation(Vector3 v, float angle){
        float half = angle*0.5f;
        float s = FloatMath.sin(half);
        x = v.x*s;
        y = v.y*s;
        z = v.z*s;
        w = FloatMath.cos(half);
	}

    /** Sets this quaternion as the angle rotation around axis v */
    public void setRotation(float x, float y, float z, float angle){
        float half = angle*0.5f;
        float s = FloatMath.sin(half);
        this.x = x*s;
        this.y = y*s;
        this.z = z*s;
        this.w = FloatMath.cos(half);
    }
	
	public void invert(){
		Quaternion conj = this.conjugate();
		float length = x * x + y * y + z * z + w * w;
		length = 1.f/length;
		conj.x *= length;
		conj.y *= length;
		conj.z *= length;
		conj.w *= length;
	}
	
	/** Multiply this quaternion by another quaternion and returns a new one */
	public Quaternion mul(Quaternion b) {
		Quaternion ret = new Quaternion();
		ret.x = +x *b.w + y *b.z - z *b.y + w *b.x;
		ret.y = -x *b.z + y *b.w + z *b.x + w *b.y;
		ret.z = +x *b.y - y *b.x + z *b.w + w *b.z;
		ret.w = -x *b.x - y *b.y - z *b.z + w *b.w;
		return ret;
	}

    /** Multiply this quaternion by another quaternion */
    public void mulInplace(Quaternion b){
        float xx = +this.x *b.w + this.y *b.z - this.z *b.y + this.w *b.x;
        float yy = -this.x *b.z + this.y *b.w + this.z *b.x + this.w *b.y;
        float zz = +this.x *b.y - this.y *b.x + this.z *b.w + this.w *b.z;
        float ww = -this.x *b.x - this.y *b.y - this.z *b.z + this.w *b.w;

        this.x = xx;
        this.y = yy;
        this.z = zz;
        this.w = ww;
    }
	
	/**
	 * Converts a quaternion rotation operator into a matrix.
	 */
	public Matrix4 toMatrix(){
		float x2, y2, z2, xx, xy, xz, yy, yz, zz, wx, wy, wz;
		if(null == _data)
			_data = new float[16];
		// calculate coefficients
		x2 = x + x;
		y2 = y + y;
		z2 = z + z;

		xx = x * x2;   xy = x * y2;   xz = x * z2;
		yy = y * y2;   yz = y * z2;   zz = z * z2;
		wx = w * x2;   wy = w * y2;   wz = w * z2;

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

    public void toMatrix(Matrix4 matrix){
        float x2, y2, z2, xx, xy, xz, yy, yz, zz, wx, wy, wz;

        float [] data = matrix.getAsArray();
        // calculate coefficients
        x2 = x + x;
        y2 = y + y;
        z2 = z + z;

        xx = x * x2;   xy = x * y2;   xz = x * z2;
        yy = y * y2;   yz = y * z2;   zz = z * z2;
        wx = w * x2;   wy = w * y2;   wz = w * z2;

        data[0] = 1.0f - (yy + zz);
        data[1] = xy - wz;
        data[2] = xz + wy;
        data[3] = 0.0f;

        data[4] = xy + wz;
        data[5] = 1.0f - (xx + zz);
        data[6] = yz - wx;
        data[7] = 0.0f;

        data[8] = xz - wy;
        data[9] = yz + wx;
        data[10] = 1.0f - (xx + yy);
        data[11] = 0.0f;

        data[12] = 0.0f;
        data[13] = 0.0f;
        data[14] = 0.0f;
        data[15] = 1.0f;
    }
	
	public Quaternion set (float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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
            final float x = (scale0 * this.x) + (scale1 * end.x);
            final float y = (scale0 * this.y) + (scale1 * end.y);
            final float z = (scale0 * this.z) + (scale1 * end.z);
            final float w = (scale0 * this.w) + (scale1 * end.w);
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
            return this.x == comp.x && this.y == comp.y && this.z == comp.z && this.w == comp.w;

    }

    /** Dot product between this and the other quaternion.
     * @param other the other quaternion.
     * @return this quaternion for chaining. */
    public float dot (Quaternion other) {
            return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    /** Multiplies the components of this quaternion with the given scalar.
     * @param scalar the scalar.
     * @return this quaternion for chaining. */
    public Quaternion mul (float scalar) {
    	Quaternion ret = new Quaternion();
    	ret.x = this.x * scalar;
    	ret.y = this.y * scalar;
    	ret.z = this.z * scalar;
    	ret.w = this.w * scalar;
        return ret;
    }
	
}
