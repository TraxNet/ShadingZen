package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 *
 */
public class PursuitBehaviourActionTests  extends ActivityInstrumentationTestCase2<DummyTestActivity> {
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
}
