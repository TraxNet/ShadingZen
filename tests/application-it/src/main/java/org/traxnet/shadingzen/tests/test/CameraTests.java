package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Camera;
import org.traxnet.shadingzen.math.MathUtil;
import org.traxnet.shadingzen.math.Vector2;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 *
 */
public class CameraTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public CameraTests() {
        super(DummyTestActivity.class);
    }

    void assertEquals(Vector3 expected, Vector3 vector, float epsilon){
        assertEquals(expected.x, vector.x, epsilon);
        assertEquals(expected.y, vector.y, epsilon);
        assertEquals(expected.z, vector.z, epsilon);
    }

    void assertEquals(Vector2 expected, Vector2 vector, float epsilon){
        assertEquals(expected.x, vector.x, epsilon);
        assertEquals(expected.y, vector.y, epsilon);
    }

    public void testInit(){
        Camera cam = new Camera();
        cam.setValues(new Vector3(0.f, 0.f, -3.f), 1.5f, 4.0f/3.0f, 1.f, 2600.0f);

        assertEquals(new Vector3(0.f, 0.f, -3.f), cam.getPosition(), 0.0001f);


    }

    public void testRayForViewportPoint(){
        Camera cam = new Camera();
        cam.setDirection(0.f, 0.f, 1.f);
        cam.setCameraUp(0.f, 1.f, 0.f);
        cam.setValues(new Vector3(0.f, 0.f, -3.f), MathUtil.HALF_PI, 4.0f/3.0f, 1.f, 2600.0f);
        cam.setViewportSize(800, 480);

        Vector3 ray = cam.rayDirectionForViewportCoordinate(400, 240);
        assertEquals(new Vector3(0.f, 0.f, 1.f), ray, 0.01f);

        ray = cam.rayDirectionForViewportCoordinate(800, 240);
        float cos = ray.dot(new Vector3(1.f, 0.f, 0.f));
        assertEquals(-Math.sqrt(2.f)/2.f, cos, 0.01f);

        ray = cam.rayDirectionForViewportCoordinate(400, 0);
        cos = ray.dot(new Vector3(0.f, 1.f, 0.f));
        assertEquals(0.6f, cos, 0.01f);
    }

    Vector2 projectPointViewportSpace(Camera cam, float x, float y, float z){
        float [] ret = new float[4], _point = new float[4];
        _point[0] = x;
        _point[1] = y;
        _point[2] = z;
        _point[3] = 1.f;

        cam.projectPointViewportSpace(ret, _point);

         return new Vector2(ret[0], ret[1]);
    }

    public void testProjectPoint(){
        Camera cam = new Camera();
        cam.setValues(new Vector3(0.f, 0.f, 0.f), MathUtil.HALF_PI, 4.0f/3.0f, 1.f, 2600.0f);
        cam.setDirection(0.f, 0.f, 1.f);
        cam.setCameraUp(0.f, 1.f, 0.f);
        cam.setViewportSize(800, 480);




        Vector2 point = projectPointViewportSpace(cam, 0.f, 0.f, 100.f);
        assertEquals(new Vector2(400, 240), point, 0.1f);
        point = projectPointViewportSpace(cam, 0.f, 0.f, 10.f);
        assertEquals(new Vector2(400, 240), point, 0.1f);

        Vector3 point3 = new Vector3((float)Math.sqrt(2.f)/2.f, 0.f,(float) Math.sqrt(2.f)/2.f);
        point3 = point3.mul(10.f);
        point = projectPointViewportSpace(cam, point3.x, point3.y, point3.z);
        assertEquals(new Vector2(0, 240), point, 0.1f);

        point3 = new Vector3(-(float)Math.sqrt(2.f)/2.f, 0.f,(float) Math.sqrt(2.f)/2.f);
        point3 = point3.mul(10.f);
        point = projectPointViewportSpace(cam, point3.x, point3.y, point3.z);
        assertEquals(new Vector2(800, 240), point, 0.1f);

        point3 = new Vector3(0.f, (float)Math.sqrt(2.f)/2.f*3.f/4.f,(float) Math.sqrt(2.f)/2.f);
        point3 = point3.mul(10.f);
        point = projectPointViewportSpace(cam, point3.x, point3.y, point3.z);
        assertEquals(new Vector2(400, 480), point, 0.1f);
    }
}
