package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.tests.HelloAndroidActivity;

/**
 *
 */
public class ActionTests  extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

    class MockActor extends Actor{

        @Override
        protected void onUpdate(float deltaTime) {

        }

        @Override
        public void onDraw(RenderService renderer) throws Exception {

        }

        @Override
        public void onLoad() {

        }

        @Override
        public void onUnload() {

        }
    }

    class MockAction extends Action {
        public int _timesCalledSteps = 0;
        public boolean isDone = false;
        public boolean isRegistered;
        public boolean containsTargetActor(){
            return null != _targetActor;
        }
        public boolean isCancelled(){
            return _cancelled;
        }
        public int timesCalledStep(){
           return _timesCalledSteps;
        }

        @Override
        public void step(float deltaTime) throws InvalidTargetActorException {
            _timesCalledSteps++;
        }

        @Override
        protected void onRegisterTarget() {
            isRegistered = true;
        }

        @Override
        public boolean isDone() {
            return isDone;
        }
    }

    public ActionTests() {
        super(HelloAndroidActivity.class);
    }


    public void testActionsAreAdded(){
        MockActor actor = new MockActor();
        MockAction action = new MockAction();

        actor.runAction(action);
        assertTrue(action.isRegistered);
        assertFalse(action.isCancelled());
        assertEquals(actor, action.getTarget());


    }
}
