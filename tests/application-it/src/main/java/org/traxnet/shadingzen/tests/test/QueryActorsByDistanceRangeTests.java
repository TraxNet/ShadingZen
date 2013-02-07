package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.core.queries.ActorQueryDistanceRange;
import org.traxnet.shadingzen.core.queries.ActorQueryResultCallback;
import org.traxnet.shadingzen.math.MathUtil;
import org.traxnet.shadingzen.math.Quaternion;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 7/02/13
 * Time: 6:45
 */
public class QueryActorsByDistanceRangeTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {
    int num_found = 0;

    public QueryActorsByDistanceRangeTests() {
        super(DummyTestActivity.class);
    }

    private void executeQuery(Scene scene, ActorQueryDistanceRange query, int expected) {
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

        ActorQueryDistanceRange query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);

        executeQuery(scene, query, 0);
    }

    public void testFindsNoActorsInAnEmptyScene(){
        Scene scene = new Scene();

        ActorQueryDistanceRange query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 1.f, 10.f);

        executeQuery(scene, query, 0);
    }

    public void testTheMinRangeDoesFilterOutActors(){
        Scene scene = new Scene();

        ActorQueryDistanceRange query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 0.f, 10.f);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);
        actor.setPosition(0, 0, 0);
        actor.setRotation(new Quaternion(Vector3.vectorUp, MathUtil.HALF_PI));

        executeQuery(scene, query, 1);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 2.f, 10.f);

        executeQuery(scene, query, 0);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 1.f, 10.f);

        executeQuery(scene, query, 1);
    }

    public void testTheMaxRangeDoesFilterOutActors(){
        Scene scene = new Scene();

        ActorQueryDistanceRange query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 0.f, 10.f);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);
        actor.setPosition(0, 0, 0);
        actor.setRotation(new Quaternion(Vector3.vectorUp, MathUtil.HALF_PI));

        executeQuery(scene, query, 1);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 0.f, 100.f);

        executeQuery(scene, query, 1);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 1.f, 2.f);

        executeQuery(scene, query, 1);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(12, 0, 0, 0.f, 10.f);

        executeQuery(scene, query, 0);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(11, 0, 0, 0.f, 10.f);

        executeQuery(scene, query, 1);
    }

    public void testWorksWitTranslatedActors(){
        Scene scene = new Scene();

        ActorQueryDistanceRange query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(0, 0, 0, 0.f, 10.f);

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 1);
        actor.setPosition(0, 10, 0);
        actor.setRotation(new Quaternion(Vector3.vectorUp, MathUtil.HALF_PI));

        executeQuery(scene, query, 1);

        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(11, 0, 0, 0.f, 10.f);

        executeQuery(scene, query, 0);


        query = (ActorQueryDistanceRange) scene.newActorQuery(ActorQueryDistanceRange.class);
        query.initWithRange(11, 0, 0, 9.f, 15.f);

        executeQuery(scene, query, 1);


    }
}
