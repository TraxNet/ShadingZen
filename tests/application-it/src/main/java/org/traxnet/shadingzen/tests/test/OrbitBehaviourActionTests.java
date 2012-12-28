package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

public class OrbitBehaviourActionTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    public OrbitBehaviourActionTests() {
        super(DummyTestActivity.class);
    }

    void executeTicks(Actor actor, int n, float deltatime){
        for(int i=0; i < n; i++)
            actor.onTick(deltatime);
    }

    void assertEquals(Vector3 expected, Vector3 vector, float epsilon){
        assertEquals(expected.x, vector.x, epsilon);
        assertEquals(expected.y, vector.y, epsilon);
        assertEquals(expected.z, vector.z, epsilon);
    }
    /*
    public void testOrbitAlreadyAtOrbitRange(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();
        MockActor navpoint = new MockActor();
        navpoint.getPosition().set(-40.f, 0.f, 0.f);


        OrbitBehaviourAction action = new OrbitBehaviourAction(navpoint, 40.f);
        vehicle.runAction(action);
        executeTicks(vehicle, 1000, 1.f/30.f);
        assertTrue(action.isDone());
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() <= 45.f);
        assertTrue((navpoint.getPosition().sub(vehicle.getPosition())).lengthSqrt() >= 35.f);
        assertEquals(new Vector3(1.f, 0.f, 0.0f), vehicle.getLocalRightAxis(), 0.01f);
    } */
}
