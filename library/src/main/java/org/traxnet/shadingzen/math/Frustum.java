package org.traxnet.shadingzen.math;

import org.traxnet.shadingzen.core.Camera;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 11/02/13
 * Time: 16:47
 */
public class Frustum {
    Plane planes[];

    public Plane getPlane(Planes plane) {
        switch (plane){
            case REAR:
                return planes[0];
            case FRONT:
                return planes[1];
            case RIGHT:
                return planes[2];
            case LEFT:
                return planes[3];
            case TOP:
                return planes[4];
            case BOTTOM:
                return planes[5];
        }

        return null;
    }

    public boolean isPointInside(float x, float y, float z) {
        for(int index=0; index < 6; index++){
            Plane plane = planes[index];
            if(plane.isPointBehind(x, y, z))
                return false;
        }

        return true;
    }

    public boolean isSphereInside(float x, float y, float z, float radius) {
        for(int index=0; index < 6; index++){
            Plane plane = planes[index];
            if(plane.isSphereBehind(x, y, z, radius))
                return false;
        }

        return true;
    }

    public boolean isBoundingBoxInside(BBox bbox) {
        for(int index=0; index < 6; index++){
            Plane plane = planes[index];

            Vector3 mins = bbox.getMins();
            Vector3 maxs = bbox.getMaxs();
            float x, y, z;
            x = mins.x;
            y = mins.y;
            z = mins.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = maxs.x;
            y = mins.y;
            z = mins.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = mins.x;
            y = maxs.y;
            z = mins.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = maxs.x;
            y = maxs.y;
            z = mins.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = mins.x;
            y = mins.y;
            z = maxs.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = maxs.x;
            y = mins.y;
            z = maxs.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = mins.x;
            y = maxs.y;
            z = maxs.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            x = maxs.x;
            y = maxs.y;
            z = maxs.z;

            if(!plane.isPointBehind(x, y, z))
                continue;

            return false;
        }

        return true;
    }

    public enum Planes{
        REAR,
        FRONT,
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    public Frustum(){
        planes = new Plane[6];

        for(int index=0; index < 6; index++)
            planes[index] = new Plane();
    }

    public Frustum(Camera camera){
        this();

        updatePlanesWithCamera(camera);
    }

    public void updatePlanesWithCamera(Camera camera){
        float [] clip = camera.getViewProjectionMatrix().getAsArray();

        Plane plane = getPlane(Planes.RIGHT);
        plane.eq[0] = clip[3] - clip[0];
        plane.eq[1] = clip[7] - clip[4];
        plane.eq[2] = clip[11] - clip[8];
        plane.eq[3] = clip[15] - clip[12];

        float t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

        plane = getPlane(Planes.LEFT);
        plane.eq[0] = clip[3] + clip[0];
        plane.eq[1] = clip[7] + clip[4];
        plane.eq[2] = clip[11] + clip[8];
        plane.eq[3] = clip[15] + clip[12];

        t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

        plane = getPlane(Planes.BOTTOM);
        plane.eq[0] = clip[3] + clip[1];
        plane.eq[1] = clip[7] + clip[5];
        plane.eq[2] = clip[11] + clip[9];
        plane.eq[3] = clip[15] + clip[13];

        t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

        plane = getPlane(Planes.TOP);
        plane.eq[0] = clip[3] - clip[1];
        plane.eq[1] = clip[7] - clip[5];
        plane.eq[2] = clip[11] - clip[9];
        plane.eq[3] = clip[15] - clip[13];

        t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

        plane = getPlane(Planes.FRONT);
        plane.eq[0] = clip[3] - clip[2];
        plane.eq[1] = clip[7] - clip[6];
        plane.eq[2] = clip[11] - clip[10];
        plane.eq[3] = clip[15] - clip[14];

        t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

        plane = getPlane(Planes.REAR);
        plane.eq[0] = clip[3] + clip[2];
        plane.eq[1] = clip[7] + clip[6];
        plane.eq[2] = clip[11] + clip[10];
        plane.eq[3] = clip[15] + clip[14];

        t = (float) Math.sqrt(plane.length());
        plane.eq[0] /= t;
        plane.eq[1] /= t;
        plane.eq[2] /= t;
        plane.eq[3] /= t;

    }


}
