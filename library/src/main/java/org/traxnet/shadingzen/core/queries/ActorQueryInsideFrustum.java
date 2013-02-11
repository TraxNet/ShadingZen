package org.traxnet.shadingzen.core.queries;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Frustum;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 11/02/13
 * Time: 18:01
 */
public class ActorQueryInsideFrustum extends ActorQuery {
    Frustum frustum;

    public void initWithFrustum(Frustum frustum){
        this.frustum = frustum;
    }

    @Override
    public boolean checkIntersectsAABBox(BBox box) {
        return frustum.isBoundingBoxInside(box);
    }

    @Override
    public boolean testActor(Actor actor) {
        return frustum.isBoundingBoxInside(actor.getBoundingBox());
    }

    @Override
    public void initializeFromPool() {

    }

    @Override
    public void finalizeFromPool() {

    }
}
