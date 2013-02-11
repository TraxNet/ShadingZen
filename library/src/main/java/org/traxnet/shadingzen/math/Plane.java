package org.traxnet.shadingzen.math;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 11/02/13
 * Time: 16:52
 */
public class Plane {
    float eq[];

    public Plane(){
        eq = new float[4];
    }

    public Plane(float a, float b, float c, float d){
        eq = new float[4];
        eq[0] = a;
        eq[1] = b;
        eq[2] = c;
        eq[3] = d;
    }

    public float getA(){ return eq[0]; }

    public float getB(){ return eq[1]; }

    public float getC(){ return eq[2]; }

    public float getD(){ return eq[3]; }

    public float length(){ return eq[0]*eq[0] + eq[1]*eq[1] + eq[2]*eq[2]; }

    public boolean isPointBehind(float x, float y, float z) {
        return eq[0]*x + eq[1]*y + eq[2]*z + eq[3] <= 0.f;
    }

    public boolean isSphereBehind(float x, float y, float z, float radius) {
        return eq[0]*x + eq[1]*y + eq[2]*z + eq[3] <= -radius;
    }
}
