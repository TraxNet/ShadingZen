package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.tests.DummyTestActivity;

import java.util.UUID;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 25/01/13
 * Time: 10:46
 */
public class CollisionDetectionTests  extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public CollisionDetectionTests() {
        super(DummyTestActivity.class);
    }

    public void testEmptyScene(){

        Scene scene = new Scene();
        scene.processColliders();
    }

    private MockVehicleActor createMockVehicleActors(Scene scene) {
        MockVehicleActor actorA = (MockVehicleActor) scene.spawn(MockVehicleActor.class, "actor_"+ UUID.randomUUID().toString());
        actorA.init();
        return actorA;
    }

    public void testOneObjectScene(){
        Scene scene = new Scene();

        MockVehicleActor actorA = createMockVehicleActors(scene);
        scene.processColliders();

        assertEquals(0, actorA.getNumCollisions());
    }

    public void testTwoNonIntersectingActors(){
        Scene scene = new Scene();

        MockVehicleActor actorA = createMockVehicleActors(scene);
        actorA.setCollisionRadius(1.f);
        actorA.setPosition(0, 0, 0);

        MockVehicleActor actorB = createMockVehicleActors(scene);
        actorB.setCollisionRadius(1.f);
        actorB.setMockPreviousPosition(0, 0, 2.1f);
        actorB.setPosition(0, 0, 2.1f);

        scene.onTick(1.f/30.f);
        assertEquals(0, actorA.getNumCollisions());
        assertEquals(0, actorB.getNumCollisions());
    }

    public void testTwoActorsOneMoving(){
        Scene scene = new Scene();

        MockVehicleActor actorA = createMockVehicleActors(scene);
        actorA.setCollisionRadius(1.f);
        actorA.setPosition(0, 0, 0);
        actorA.onTick(0.001f);
        actorA.setPosition(0, 0, 1);

        MockVehicleActor actorB = createMockVehicleActors(scene);
        actorB.setCollisionRadius(1.f);
        actorB.setPosition(0, 0, 0.9f);
        //actorB.onTick(0.001f);

        scene.onTick(1.f/30.f);
        assertEquals(1, actorA.getNumCollisions());
        assertEquals(1, actorB.getNumCollisions());

    }

    public void testTwoActorsBothStatic(){
        Scene scene = new Scene();

        MockVehicleActor actorA = createMockVehicleActors(scene);
        actorA.setCollisionRadius(1.f);
        actorA.setPosition(0, 0, 0);

        MockVehicleActor actorB = createMockVehicleActors(scene);
        actorB.setCollisionRadius(1.f);
        actorB.setMockPreviousPosition(0.f, 0, 1.0f);
        actorB.setPosition(0, 0, 1.0f);
        //actorB.onTick(0.001f);

        scene.onTick(1.f/30.f);
        assertEquals(1, actorA.getNumCollisions());
        assertEquals(1, actorB.getNumCollisions());

    }
}
