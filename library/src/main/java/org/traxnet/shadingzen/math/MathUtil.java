package org.traxnet.shadingzen.math;

public class MathUtil {
	/** PI constant as float */
	public static float PI = 3.141592653589f;
	/** PI/2 constant as float */
	public static float HALF_PI = 1.5707963267946f;
	/** Convert radians to degrees */
	public static float toDegrees(float radians){
		return radians*180/PI;
	}
	/** Convert degrees to radians */
	public static float toRadians(float degrees){
		return degrees*PI/180;
	}
}
