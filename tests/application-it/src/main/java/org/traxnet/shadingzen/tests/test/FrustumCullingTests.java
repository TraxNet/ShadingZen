package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Camera;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Frustum;
import org.traxnet.shadingzen.math.Plane;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 11/02/13
 * Time: 16:45
 */
public class FrustumCullingTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public FrustumCullingTests() {
        super(DummyTestActivity.class);
    }


    public void testCanGetPlanes(){
        Frustum frustum = createTestFrustum();
        Plane plane = frustum.getPlane(Frustum.Planes.REAR);
        assertNotNull(plane);
        plane = frustum.getPlane(Frustum.Planes.FRONT);
        assertNotNull(plane);
        plane = frustum.getPlane(Frustum.Planes.LEFT);
        assertNotNull(plane);
        plane = frustum.getPlane(Frustum.Planes.RIGHT);
        assertNotNull(plane);
        plane = frustum.getPlane(Frustum.Planes.TOP);
        assertNotNull(plane);
        plane = frustum.getPlane(Frustum.Planes.BOTTOM);
        assertNotNull(plane);

    }

    private Frustum createTestFrustum() {
        Camera camera = new Camera();
        camera.setValues(Vector3.zero, 1.5f, 4.0f/3.0f, 1.f, 2600.0f);
        camera.setDirection(0, 0, 1);

        return new Frustum(camera);
    }

    public void testRandomPoints(){
        Frustum frustum = createTestFrustum();

        assertTrue(frustum.isPointInside(0, 0, 40));
        assertTrue(frustum.isPointInside(0, 0, 1.0001f));
        assertFalse(frustum.isPointInside(0, 0, 0));
        assertFalse(frustum.isPointInside(0, 0, -2));
        assertFalse(frustum.isPointInside(0, 10, 1));
        assertFalse(frustum.isPointInside(0, -10, 1));
    }

    public void testSpheres(){
        Frustum frustum = createTestFrustum();

        assertTrue(frustum.isSphereInside(0, 0, 0, 2));
        assertTrue(frustum.isSphereInside(0, 0, 2600, 2));
        assertFalse(frustum.isSphereInside(0, 0, -2, 2));
    }


    public void testBoundingBoxes(){
        Frustum frustum = createTestFrustum();

        BBox bbox = new BBox();
        bbox.setFromRadius(Vector3.zero, 1.f);
        assertFalse(frustum.isBoundingBoxInside(bbox));

        bbox.setFromRadius(Vector3.zero, 2.f);
        assertTrue(frustum.isBoundingBoxInside(bbox));

        bbox.setFromRadius(new Vector3(0, 0, 2602.f), 1.f);
        assertFalse(frustum.isBoundingBoxInside(bbox));

        bbox.setFromRadius(new Vector3(0, 0, 2601), 2.f);
        assertTrue(frustum.isBoundingBoxInside(bbox));

    }
}
