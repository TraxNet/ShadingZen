package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.ai.ApproachBehaviourAction;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 *
 */
public class ApproachBehaviourActionTests  extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public ApproachBehaviourActionTests() {
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

    public void testApproachUp(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(0.f, 100.f, 0.f);


        ApproachBehaviourAction action = new ApproachBehaviourAction(navpoint.getPosition(), 5.f, false);
        vehicle.runAction(action);
        executeTicks(vehicle, action, 1000, 1.f/30.f);
        assertTrue(action.isDone());
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() <= 5.f);
        assertEquals(new Vector3(1.f, 0.f, 0.0f), vehicle.getLocalRightAxis(), 0.01f);
        assertEquals(new Vector3(0.f, 0.f, -1.0f), vehicle.getLocalUpAxis(), 0.1f);
    }

    public void testApproachDown(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(0.f, -100.f, 0.f);


        ApproachBehaviourAction action = new ApproachBehaviourAction(navpoint.getPosition(), 5.f, false);
        vehicle.runAction(action);
        executeTicks(vehicle, action, 1000, 1.f/30.f);
        assertTrue(action.isDone());
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() <= 5.f);
        assertEquals(new Vector3(1.f, 0.f, 0.0f), vehicle.getLocalRightAxis(), 0.01f);
        assertEquals(new Vector3(0.f, 0.f, 1.0f), vehicle.getLocalUpAxis(), 0.1f);
    }

    public void testApproachRight(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(100.f, 0.f, 0.f);


        ApproachBehaviourAction action = new ApproachBehaviourAction(navpoint.getPosition(), 5.f, false);
        vehicle.runAction(action);
        executeTicks(vehicle, action, 1000, 1.f/30.f);
        assertTrue(action.isDone());
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() <= 5.f);
        assertEquals(new Vector3(0.f, 0.f, -1.0f), vehicle.getLocalRightAxis(), 0.1f);
        assertEquals(new Vector3(0.f, 1.f, 0.0f), vehicle.getLocalUpAxis(), 0.01f);
    }
    public void testApproachLeft(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(-100.f, 0.f, 0.f);


        ApproachBehaviourAction action = new ApproachBehaviourAction(navpoint.getPosition(), 5.f, false);
        vehicle.runAction(action);
        executeTicks(vehicle, action, 1000, 1.f/30.f);
        assertTrue(action.isDone());
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() <= 5.f);
        assertEquals(new Vector3(0.f, 0.f, 1.0f), vehicle.getLocalRightAxis(), 0.1f);
        assertEquals(new Vector3(0.f, 1.f, 0.0f), vehicle.getLocalUpAxis(), 0.01f);
    }


    public void testCancel(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(100.f, 0.f, 0.f);


        ApproachBehaviourAction action = new ApproachBehaviourAction(navpoint.getPosition(), 5.f, false);
        vehicle.runAction(action);
        executeTicks(vehicle, action, 10, 1.f/30.f);
        action.cancel();
        executeTicks(vehicle, action, 90, 1.f/30.f);
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() > 5.f);
        assertEquals(new Vector3(0.f, 1.f, 0.0f), vehicle.getLocalUpAxis(), 0.01f);
    }
}
