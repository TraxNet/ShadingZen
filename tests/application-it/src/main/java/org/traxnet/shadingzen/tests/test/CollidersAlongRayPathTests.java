package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.core.CollisionInfo;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 27/01/13
 * Time: 11:47
 */
public class CollidersAlongRayPathTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public CollidersAlongRayPathTests() {
        super(DummyTestActivity.class);
    }

    float [] orig = new float[4];
    float [] dir = new float[4];

    private Scene createTestSceneAndRay( Vector3 origin, Vector3 direction) {
        Scene scene = new Scene();

        origin.toArray(orig);
        direction.toArray(dir);
        return scene;
    }

    private void createMockActorAtPosition(Scene scene, String name, Collider.CollidableStatus collidable, Vector3 position) {
        MockVehicleActor actorA = (MockVehicleActor) scene.spawn(MockVehicleActor.class, name);
        actorA.setCollisionRadius(1.f);
        actorA.setCollidableStatus(collidable);
        actorA.setPosition(position);
    }

    @Override
    protected void setUp(){

    }

    public void testGetEmptyList(){
       Scene scene = createTestSceneAndRay(Vector3.zero, Vector3.vectorFront);

       assertNull(scene.getNearestColliderAlongRay(null, orig, dir, 10.f, 0.25f));
    }

    public void testOneHit(){
        Scene scene = createTestSceneAndRay(Vector3.zero, Vector3.vectorFront);

        createMockActorAtPosition(scene, "actorA", Collider.CollidableStatus.FULL_COLLIDABLE, Vector3.vectorFront.mul(9.f));

        scene.onTick(1.f/30.f);

        CollisionInfo info = scene.getNearestColliderAlongRay(null, orig, dir, 10.f, 0.25f);
        assertNotNull(info);

    }

    public void testIsCorrectlySortedByLength(){
        Scene scene = createTestSceneAndRay(Vector3.zero, Vector3.vectorFront);

        createMockActorAtPosition(scene, "actorA", Collider.CollidableStatus.FULL_COLLIDABLE, Vector3.vectorFront.mul(5.f));
        createMockActorAtPosition(scene, "actorB", Collider.CollidableStatus.FULL_COLLIDABLE, Vector3.vectorFront.mul(9.f));
        createMockActorAtPosition(scene, "actorC", Collider.CollidableStatus.FULL_COLLIDABLE, Vector3.vectorFront.mul(5.5f));
        createMockActorAtPosition(scene, "actorD", Collider.CollidableStatus.FULL_COLLIDABLE, Vector3.vectorFront.mul(19.f));


        scene.onTick(1.f/30.f);

        CollisionInfo info = scene.getNearestColliderAlongRay(null, orig, dir, 10.f, 0.25f);
        assertNotNull(info);
        assertEquals("actorA", info.hitActor.getNameId());
    }
}
