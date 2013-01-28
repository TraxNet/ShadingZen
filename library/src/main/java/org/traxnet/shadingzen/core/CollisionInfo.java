package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Vector3;

/**
 */
public class CollisionInfo implements Comparable{
    public Vector3 hitPoint = new Vector3();
    public Vector3 hitNormal = new Vector3();
    public Collider hitActor;
    public float hitLength;

    @Override
    public int compareTo(Object o) {
        CollisionInfo other = (CollisionInfo)o;

        if(other.hitLength > hitLength)
            return -1;
        else if(other.hitLength < hitLength)
            return 1;
        else
            return 0;
    }

    public void setZeros() {
        hitPoint.set(0, 0, 0);
        hitNormal.set(0, 0, 0);
        hitLength = 0;

    }
}
