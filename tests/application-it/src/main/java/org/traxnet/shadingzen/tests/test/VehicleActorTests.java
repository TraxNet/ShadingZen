package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 *
 */
public class VehicleActorTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {


    public VehicleActorTests() {
            super(DummyTestActivity.class);
    }
      /*
    void executeTicksWithDesiredSteering(VehicleActor actor, Vector3 steering, int n, float deltatime){
        for(int i=0; i < n; i++) {
            actor.addVelocityVector(steering.x, steering.y, steering.z);
            actor.onTick(deltatime);
        }
    }  */

    void executeTicks(Actor actor, int n, float deltatime){
        for(int i=0; i < n; i++)
            actor.onTick(deltatime);
    }

    void assertEquals(Vector3 expected, Vector3 vector, float epsilon){
        assertEquals(expected.x, vector.x, epsilon);
        assertEquals(expected.y, vector.y, epsilon);
        assertEquals(expected.z, vector.z, epsilon);
    }

    public void testAccelerationDeceleration(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();

        vehicle.setAccelerateState(VehicleActor.AccelerateState.MAINTAIN_VELOCITY);
        executeTicks(vehicle, 100, 1.f / 30.f); // 30 fps
        assertEquals(Vector3.zero, vehicle.getPosition(), 0.01f);
        assertEquals(0.f, vehicle.getCurrentVelocity(), 0.01f);

        vehicle.setTargetFrontVelocity(100.f);
        vehicle.setAccelerateState(VehicleActor.AccelerateState.ACCELERATE);
        executeTicks(vehicle, 1000, 1.f / 30.f);
        assertTrue(vehicle.getPosition().z > 100.f);
        assertEquals(vehicle.getMaxVelocity(), vehicle.getCurrentVelocity(), 0.01f);

        vehicle.setAccelerateState(VehicleActor.AccelerateState.DECELERATE);
        executeTicks(vehicle, 1000, 1.f / 30.f);
        assertEquals(0.f, vehicle.getCurrentVelocity(), 0.01f);
        executeTicks(vehicle, 1000, 1.f / 30.f);
        assertTrue(vehicle.getPosition().z > 100.f);
        Log.i("ShadingZen", "--->"+vehicle.getCurrentVelocity());
        assertEquals(0.f, vehicle.getCurrentVelocity(), 0.1f);
    }
     /*
     public void testSteeringRight(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();

        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        executeTicksWithDesiredSteering(vehicle, new Vector3(500000.f, 0.f, 0.f), 1000, 1.f / 60.f);
        assertTrue(vehicle.getPosition().x > 50.f);
        assertTrue(Math.abs(vehicle.getPosition().z) < 5.f);
        assertEquals(0.f, vehicle.getPosition().y, 0.01f);
        //assertEquals(vehicle.getMaxVelocity(), vehicle.getCurrentVelocity(), 0.01f);
        // Check vehicle is facing the appropriate direction
        assertEquals(new Vector3(0.f, 1.f, 0.f), vehicle.getLocalUpAxis(), 0.01f);
        assertEquals(new Vector3(1.f, 0.f, 0.f), vehicle.getLocalFrontAxis(), 0.1f);
        assertEquals(new Vector3(0.f, 0.f, -1.f), vehicle.getLocalRightAxis(), 0.1f);
    }

    public void testSteeringLeft(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();

        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        executeTicksWithDesiredSteering(vehicle, new Vector3(-500000.f, 0.f, 0.f), 1000, 1.f / 60.f);
        assertTrue(vehicle.getPosition().x < -50.f);
        assertTrue(Math.abs(vehicle.getPosition().z) < 5.f);
        assertEquals(0.f, vehicle.getPosition().y, 0.01f);
        //assertEquals(vehicle.getMaxVelocity(), vehicle.getCurrentVelocity(), 0.01f);
        // Check vehicle is facing the appropriate direction
        assertEquals(new Vector3(0.f, 1.f, 0.f), vehicle.getLocalUpAxis(), 0.01f);
        assertEquals(new Vector3(-1.f, 0.f, 0.f), vehicle.getLocalFrontAxis(), 0.1f);
        assertEquals(new Vector3(0.f, 0.f, 1.f), vehicle.getLocalRightAxis(), 0.1f);
    }

    public void testSteeringUp(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();

        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        executeTicksWithDesiredSteering(vehicle, new Vector3(0.f, 50000.f, 0.f), 1000, 1.f / 60.f);
        assertTrue(vehicle.getPosition().y > 50.f);
        assertTrue(Math.abs(vehicle.getPosition().z) < 5.f);
        assertEquals(0.f, vehicle.getPosition().x, 0.01f);
        //assertEquals(vehicle.getMaxVelocity(), vehicle.getCurrentVelocity(), 0.01f);
        // Check vehicle is facing the appropriate direction
        assertEquals(new Vector3(0.f, 0.f, -1.f), vehicle.getLocalUpAxis(), 0.1f);
        assertEquals(new Vector3(0.f, 1.f, 0.f), vehicle.getLocalFrontAxis(), 0.1f);
        assertEquals(new Vector3(1.f, 0.f, 0.f), vehicle.getLocalRightAxis(), 0.01f);
    }

    public void testSteeringDown(){
        MockVehicleActor vehicle = new MockVehicleActor();
        vehicle.init();

        vehicle.setAccelerateState(VehicleActor.AccelerateState.AUTO);
        executeTicksWithDesiredSteering(vehicle, new Vector3(0.f, -50000.f, 0.f), 1000, 1.f / 60.f);
        assertTrue(vehicle.getPosition().y < 50.f);
        assertTrue(Math.abs(vehicle.getPosition().z) < 5.f);
        assertEquals(0.f, vehicle.getPosition().x, 0.01f);
        //assertEquals(vehicle.getMaxVelocity(), vehicle.getCurrentVelocity(), 0.01f);
        // Check vehicle is facing the appropriate direction
        assertEquals(new Vector3(0.f, 0.f, 1.f), vehicle.getLocalUpAxis(), 0.1f);
        assertEquals(new Vector3(0.f, -1.f, 0.f), vehicle.getLocalFrontAxis(), 0.1f);
        assertEquals(new Vector3(1.f, 0.f, 0.f), vehicle.getLocalRightAxis(), 0.01f);
    }
     */
}
