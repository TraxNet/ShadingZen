package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.simulation.ActionsDrivenBehaviouralState;
import org.traxnet.shadingzen.simulation.BehaviourAction;
import org.traxnet.shadingzen.simulation.BehaviourArbitrator;
import org.traxnet.shadingzen.simulation.ai.VehicleActor;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 */
public class BehaviourTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {


    class MockBehaviouralState extends ActionsDrivenBehaviouralState {
        public int timesCalledStep = 0;
        public boolean shouldTakeOver = false;
        public boolean suppressIsCalled = false;
        public MockBehaviourAction mockAction = null;

        public MockBehaviouralState(){
            mockAction = new  MockBehaviourAction();
        }

        public Actor getTargetActor(){ return _targetActor; }


        public void register(VehicleActor target) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void step(float deltaTime) {
            timesCalledStep++;

            if(timesCalledStep == 1){
                runAction(mockAction);
            }
        }

        public boolean takeOver() {
            return shouldTakeOver;
        }


        @Override
        public void suppress() {
            super.suppress();

            suppressIsCalled = true;
        }

        public void start() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    class MockBehaviourAction extends BehaviourAction {
        public boolean isRegistered = false;

        public Actor getTargetActor(){ return _targetActor; }

        @Override
        public void step(float deltaTime) throws InvalidTargetActorException {

        }

        @Override
        protected void onRegisterTarget() {
            isRegistered = true;
        }

        @Override
        public boolean isDone() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public BehaviourTests() {
        super(DummyTestActivity.class);
    }


    public void testBehaviourArbitrator(){
        MockActor actor = new MockActor();
        MockBehaviouralState state = new MockBehaviouralState();

        BehaviourArbitrator arbitrator = new BehaviourArbitrator(2);
        arbitrator.registerBehaviour(state, 0);

        actor.runAction(arbitrator);

        assertFalse(state.suppressIsCalled);
        assertNotNull(state.getTargetActor());

        actor.removeAction(arbitrator);
        assertTrue(state.suppressIsCalled);


    }

    public void testActionsDrivenBehaviourState(){
        MockActor actor = new MockActor();
        MockBehaviouralState state01 = new MockBehaviouralState();

        BehaviourArbitrator arbitrator = new BehaviourArbitrator(2);
        arbitrator.registerBehaviour(state01, 0);

        actor.runAction(arbitrator);
        assertEquals(0, state01.timesCalledStep);
        assertEquals(false, state01.suppressIsCalled);

        // Check the state takes over
        state01.shouldTakeOver = true;
        actor.onTick(0.0f);
        assertEquals(1, state01.timesCalledStep);

        assertTrue(state01.mockAction.isRegistered);

    }

    public void testBehaviourArbitratorDecisorCode(){
        MockActor actor = new MockActor();
        MockBehaviouralState state01 = new MockBehaviouralState();
        MockBehaviouralState state02 = new MockBehaviouralState();
        MockBehaviouralState state03 = new MockBehaviouralState();
        MockBehaviouralState state04 = new MockBehaviouralState();

        BehaviourArbitrator arbitrator = new BehaviourArbitrator(2);
        arbitrator.registerBehaviour(state01, 0);
        arbitrator.registerBehaviour(state02, 0);
        arbitrator.registerBehaviour(state03, 2);
        arbitrator.registerBehaviour(state04, 3);

        actor.runAction(arbitrator);
        assertEquals(0, state01.timesCalledStep);
        assertEquals(false, state01.suppressIsCalled);

        // Check the state01 takes over
        state01.shouldTakeOver = true;
        actor.onTick(0.0f);
        assertEquals(1, state01.timesCalledStep);

        assertTrue(state01.mockAction.isRegistered);

        // Check the state02 does NOT takes over (lower priority, active has +2 bonus)
        state02.shouldTakeOver = true;
        actor.onTick(0.0f);
        assertEquals(2, state01.timesCalledStep);
        assertEquals(0, state02.timesCalledStep);

        // Check the state03 does NOT takes over (similar priority)
        state03.shouldTakeOver = true;
        actor.onTick(0.0f);
        assertEquals(3, state01.timesCalledStep);
        assertEquals(0, state02.timesCalledStep);
        assertEquals(0, state03.timesCalledStep);

        // Check the state04 takes over
        state04.shouldTakeOver = true;
        actor.onTick(0.0f);
        assertEquals(3, state01.timesCalledStep);
        assertEquals(0, state02.timesCalledStep);
        assertEquals(0, state03.timesCalledStep);
        assertEquals(1, state04.timesCalledStep);
    }
}
