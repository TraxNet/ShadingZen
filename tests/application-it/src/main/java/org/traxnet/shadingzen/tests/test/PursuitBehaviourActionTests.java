package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.simulation.ai.ApproachBehaviourAction;
import org.traxnet.shadingzen.simulation.ai.PursuitBehaviourAction;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 *
 */
public class PursuitBehaviourActionTests  extends ActivityInstrumentationTestCase2<DummyTestActivity> {

    private MockVehicleActor quarry;
    private MockVehicleActor pursuer;
    private Scene scene;

    public PursuitBehaviourActionTests() {
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

    private void createQuarryAndPursuer(Vector3 quarry_position) {
        scene = new Scene();
        quarry = createMockActorAtPosition(
                scene, "quarry", Collider.CollidableStatus.COLLIDABLE_BY_OTHERS, quarry_position);

        pursuer = createMockActorAtPosition(
                scene, "pursuer", Collider.CollidableStatus.COLLIDABLE_BY_OTHERS, new Vector3(0, 0, 0.f));
        pursuer.setAccelerateState(VehicleActor.AccelerateState.AUTO);
    }

    public void testCanRunAction(){
        createQuarryAndPursuer(new Vector3(0, 0, 300.f));

        PursuitBehaviourAction action = new PursuitBehaviourAction(quarry, 1.f, false);
        pursuer.runAction(action);

        scene.onTick(1.f/30.f);

        assertEquals(new Vector3(0, 0, 300.f), quarry.getPosition(), 0.0001f);
        assertFalse(action.isDone());
        assertNotNull(action.getTarget());
    }

    public void testArrivesToStaticQuarry(){
        createQuarryAndPursuer(new Vector3(0, 0, 300.f));

        quarry.setMaxVelocity(0.f);
        pursuer.setMaxVelocity(5.f);
        pursuer.setTargetFrontVelocity(5.f);

        PursuitBehaviourAction action = new PursuitBehaviourAction(quarry, 1.f, false);
        pursuer.runAction(action);

        executeTicks(pursuer, action, 3000, 1.f/30.f);

        assertTrue(action.isDone());
        float distance = quarry.getPosition().sub(pursuer.getPosition()).lengthSqrt();
        assertTrue(distance <= 1.f);

    }

    public void testArrivesToMovingQuarry(){
        createQuarryAndPursuer(new Vector3(0, 0, 100.f));

        quarry.setMaxVelocity(1.f);
        quarry.setAccelerateState(VehicleActor.AccelerateState.ACCELERATE);
        pursuer.setMaxVelocity(5.f);
        pursuer.setTargetFrontVelocity(5.f);

        PursuitBehaviourAction action = new PursuitBehaviourAction(quarry, 1.f, false);
        pursuer.runAction(action);

        executeTicks(pursuer, action, 1000, 1.f/30.f);

        assertTrue(quarry.getPosition().z >= 100.f);
        assertTrue(quarry.getPosition().x == 0.f);
        assertTrue(quarry.getPosition().y == 0.f);

        assertTrue(pursuer.getPosition().z >= 10.f);
        assertTrue(pursuer.getPosition().x == 0.f);
        assertTrue(pursuer.getPosition().y == 0.f);

        assertTrue(action.isDone());
        float distance = quarry.getPosition().sub(pursuer.getPosition()).lengthSqrt();
        assertTrue(distance <= 1.f);
    }

    public void testArrivesToMoreComplexQuarryMovement(){
        createQuarryAndPursuer(new Vector3(0, 0, 100.f));

        ApproachBehaviourAction approach_action = new ApproachBehaviourAction(new Vector3(4000, 0, 4000), 10, false);
        quarry.runAction(approach_action);

        quarry.setMaxVelocity(3.f);
        quarry.setAccelerateState(VehicleActor.AccelerateState.ACCELERATE);
        pursuer.setMaxVelocity(5.f);
        pursuer.setTargetFrontVelocity(5.f);

        PursuitBehaviourAction action = new PursuitBehaviourAction(quarry, 1.f, false);
        pursuer.runAction(action);

        //pursuer.enablePositionLogging();

        executeTicks(scene, action, 3000, 1.f/30.f);

        assertTrue(quarry.getPosition().z > 100.f);
        assertTrue(quarry.getPosition().x > 0.f);
        assertTrue(quarry.getPosition().y == 0.f);

        assertTrue(pursuer.getPosition().z > 100.f);
        assertTrue(pursuer.getPosition().x > 0.f);
        assertTrue(pursuer.getPosition().y == 0.f);

        assertTrue(action.isDone());
        float distance = quarry.getPosition().sub(pursuer.getPosition()).lengthSqrt();
        assertTrue(distance <= 1.f);

    }

    public void testCanTurnAroundAndSeek(){
        createQuarryAndPursuer(new Vector3(0, 0, -300.f));

        ApproachBehaviourAction approach_action = new ApproachBehaviourAction(new Vector3(4000, 0, -4000), 10, false);
        quarry.runAction(approach_action);

        quarry.setMaxVelocity(1.f);
        quarry.setAccelerateState(VehicleActor.AccelerateState.ACCELERATE);
        pursuer.setMaxVelocity(5.f);
        pursuer.setTargetFrontVelocity(5.f);

        PursuitBehaviourAction action = new PursuitBehaviourAction(quarry, 1.f, false);
        pursuer.runAction(action);

        pursuer.enablePositionLogging();

        executeTicks(scene, action, 5000, 1.f/30.f);

        assertTrue(action.isDone());
        float distance = quarry.getPosition().sub(pursuer.getPosition()).lengthSqrt();
        assertTrue(distance <= 1.f);

    }
}
