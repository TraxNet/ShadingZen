package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.core.queries.ActorQuery;
import org.traxnet.shadingzen.core.queries.ActorQueryByClass;
import org.traxnet.shadingzen.core.queries.ActorQueryResultCallback;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 5:50
 */
public class QuerySceneActorsTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {

    public QuerySceneActorsTests() {
        super(DummyTestActivity.class);
    }

    class MockActorQuery extends ActorQuery {
        public int timesCalled_checkIntersectsAABBox = 0;
        public int timesCalled_checkIntersectsActor = 0;

        public boolean checkIntersectsAABBox(BBox box) {
            timesCalled_checkIntersectsAABBox++;
            return true;
        }

        public boolean testActor(Actor actor) {
            timesCalled_checkIntersectsActor++;
            return true;
        }

        public void initializeFromPool() {

        }

        public void finalizeFromPool() {

        }
    }

    int num_found = 0;

    public void testCanCreateAGenericQuery(){
        Scene scene = new Scene();
        ActorQuery query = new MockActorQuery();

        num_found = 0;

        scene.runActorQuery(query, new ActorQueryResultCallback(){

            public void onActorFound(Actor actor) {
                num_found++;
            }
        });

        assertEquals(0, num_found);
    }

    public void testCanFindObjectsInScene(){
        Scene scene = new Scene();
        ActorQuery query = new MockActorQuery();

        num_found = 0;

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 10.f);
        actor.setPosition(0, 0, 0);

        scene.runActorQuery(query, new ActorQueryResultCallback(){
            public void onActorFound(Actor actor) {
                num_found++;
            }
        });

        assertEquals(1, num_found);
    }

    public void testCanFindActorByClass(){
        Scene scene = new Scene();
        ActorQueryByClass query = scene.newQueryByClass();
        query.setSearchClass(MockActor.class);

        num_found = 0;

        MockActor actor = (MockActor) scene.spawn(MockActor.class, "actor01");
        actor.getBoundingBox().setFromRadius(Vector3.zero, 10.f);
        actor.setPosition(0, 0, 0);

        scene.runActorQuery(query, new ActorQueryResultCallback(){
            public void onActorFound(Actor actor) {
                num_found++;
            }
        });

        assertEquals(1, num_found);

        num_found = 0;

        query = scene.newQueryByClass();
        query.setSearchClass(MockVehicleActor.class);
        scene.runActorQuery(query, new ActorQueryResultCallback(){
            public void onActorFound(Actor actor) {
                num_found++;
            }
        });

        assertEquals(0, num_found);
    }
}
