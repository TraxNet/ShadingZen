package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Vector3;

/**
 */
public class CollisionInfo{
    public Vector3 hitPoint = new Vector3();
    public Vector3 hitNormal = new Vector3();
    public Collider hitActor;
    public float hitLength;
}
