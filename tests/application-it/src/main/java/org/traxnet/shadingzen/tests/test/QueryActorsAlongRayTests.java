package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.core.queries.ActorQueryAlongRay;
import org.traxnet.shadingzen.core.queries.ActorQueryResultCallback;
import org.traxnet.shadingzen.math.MathUtil;
import org.traxnet.shadingzen.math.Quaternion;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 7:38
 */
public class QueryActorsAlongRayTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {

    int num_found = 0;

    public QueryActorsAlongRayTests() {
            super(DummyTestActivity.class);
    }

    private void executeQuery(Scene scene, ActorQueryAlongRay query, int expected) {
        num_found = 0;

        // Advance the scene 1 frame, needed to calculate some data
        scene.onTick(1/30.f);

        scene.runActorQuery(query, new ActorQueryResultCallback() {
            public void onActorFound(Actor actor) {
                num_found++;
            }
        });

        assertEquals(expected, num_found);
    }

    public void testCanCreateQueryObject(){
        Scene scene = new Scene();

        ActorQueryAlongRay query = (ActorQueryAlongRay) scene.newActorQuery(ActorQueryAlongRay.class);

        executeQuery(scene, query, 0);
    }

    public void testCanFindIntersectingObject(){
        float [] origin = Vector4.zero().getAsArray();
        float [] dir = (new Vector4(0, 0, 1, 0)).getAsArray();

        Scene scene = new Scene();
        ActorQueryAlongRay query = (ActorQueryAlongRay) scene.newActorQuery(ActorQueryAlongRay.class);

        query.setRay(origin, dir, 1, 10);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);

        executeQuery(scene, query, 1);
    }

    public void testFailsToFindNonIntersectingActor(){
        float [] origin = (new Vector4(10, 0, 0, 0)).getAsArray();
        float [] dir = (new Vector4(0, 0, 1, 0)).getAsArray();

        Scene scene = new Scene();
        ActorQueryAlongRay query = (ActorQueryAlongRay) scene.newActorQuery(ActorQueryAlongRay.class);

        query.setRay(origin, dir, 1, 10);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);

        executeQuery(scene, query, 0);
    }


    public void testWorksWithAffinedTransformedActors(){
        float [] origin = (new Vector4(10, 0, 0, 0)).getAsArray();
        float [] dir = (new Vector4(0, 0, 1, 0)).getAsArray();

        Scene scene = new Scene();
        ActorQueryAlongRay query = (ActorQueryAlongRay) scene.newActorQuery(ActorQueryAlongRay.class);

        query.setRay(origin, dir, 1, 10);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);
        actor.setPosition(10, 0, 5);
        actor.setRotation(new Quaternion(Vector3.vectorUp, MathUtil.HALF_PI));

        executeQuery(scene, query, 1);
    }

    public void testFailsToFindAffinedTransformedActor(){
        float [] origin = (new Vector4(10, 0, 0, 0)).getAsArray();
        float [] dir = (new Vector4(0, 0, 1, 0)).getAsArray();

        Scene scene = new Scene();
        ActorQueryAlongRay query = (ActorQueryAlongRay) scene.newActorQuery(ActorQueryAlongRay.class);

        query.setRay(origin, dir, 1, 10);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);
        actor.setPosition(100, 0, 5);
        actor.setRotation(new Quaternion(Vector3.vectorUp, MathUtil.HALF_PI));

        executeQuery(scene, query, 0);
    }
}
