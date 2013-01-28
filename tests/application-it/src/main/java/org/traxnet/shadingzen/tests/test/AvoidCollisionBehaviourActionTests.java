package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.BehaviourArbitrator;
import org.traxnet.shadingzen.simulation.ai.AvoidCollisionBehaviourState;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 25/01/13
 * Time: 7:42
 */
public class AvoidCollisionBehaviourActionTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public AvoidCollisionBehaviourActionTests() {
        super(DummyTestActivity.class);
    }

    void executeTicks(Actor actor, Action check, int n, float deltatime){
        for(int i=0; i < n && !check.isDone(); i++)
            actor.onTick(deltatime);
    }

    void assertEquals(Vector3 expected, Vector3 vector, float epsilon){
        assertEquals(expected.x, vector.x, epsilon);
        assertEquals(expected.y, vector.y, epsilon);
        assertEquals(expected.z, vector.z, epsilon);
    }

    private MockVehicleActor createMockActorAtPosition(Scene scene, String name, Collider.CollidableStatus collidable, Vector3 position) {
        MockVehicleActor actorA = (MockVehicleActor) scene.spawn(MockVehicleActor.class, name);
        actorA.setCollisionRadius(1.f);
        actorA.setCollidableStatus(collidable);
        actorA.setPosition(position);
        return actorA;
    }

    public void testObjectIsInsideObstacle(){
        Engine engine = new Engine(640, 480);

        Scene scene = new Scene();
        engine.pushScene(scene);

        MockVehicleActor vehicle, obstacle;
        vehicle = createMockActorAtPosition(scene, "tester",
                Collider.CollidableStatus.FULL_COLLIDABLE, new Vector3(Vector3.zero));
        obstacle = createMockActorAtPosition(scene, "obstacle1",
                Collider.CollidableStatus.COLLIDABLE_BY_OTHERS, new Vector3(0, 0, 0));

        AvoidCollisionBehaviourState state = new AvoidCollisionBehaviourState(10, 1);
        BehaviourArbitrator arbitrator = new BehaviourArbitrator(1);
        arbitrator.registerBehaviour(state, 2);

        vehicle.runAction(arbitrator);
        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        vehicle.setTargetFrontVelocity(0.f);


        for(int i=0; i < 1000; i++)
            scene.onTick(1.f / 30.f);

        assertTrue(vehicle.getNumCollisions() > 0);
        assertTrue(vehicle.getCurrentVelocity() > 0.f);
        assertFalse(vehicle.isCurrentlyColliding());

        engine.popScene();
    }

    public void testObstacleInFront(){
        Engine engine = new Engine(640, 480);

        Scene scene = new Scene();
        engine.pushScene(scene);

        MockVehicleActor vehicle, obstacle;
        vehicle = createMockActorAtPosition(scene, "tester",
                Collider.CollidableStatus.FULL_COLLIDABLE, new Vector3(Vector3.zero));
        obstacle = createMockActorAtPosition(scene, "obstacle1",
                Collider.CollidableStatus.COLLIDABLE_BY_OTHERS, new Vector3(0, 0, 10));

        AvoidCollisionBehaviourState state = new AvoidCollisionBehaviourState(20, 1);
        BehaviourArbitrator arbitrator = new BehaviourArbitrator(1);
        arbitrator.registerBehaviour(state, 2);

        vehicle.runAction(arbitrator);
        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        vehicle.setTargetFrontVelocity(10.f);


        for(int i=0; i < 1000; i++)
            scene.onTick(1.f / 30.f);

        assertTrue(vehicle.getNumCollisions() == 0);
        assertTrue(vehicle.getCurrentVelocity() > 0.f);
        assertFalse(vehicle.isCurrentlyColliding());
        assertFalse(vehicle.getPosition().z == 0);

        engine.popScene();
    }


}
