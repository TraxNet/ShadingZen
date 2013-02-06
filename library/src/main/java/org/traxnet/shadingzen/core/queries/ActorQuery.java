package org.traxnet.shadingzen.core.queries;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.util.Poolable;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 5:56
 */
public abstract class ActorQuery implements Poolable {
    public abstract boolean checkIntersectsAABBox(BBox box);
    public abstract boolean testActor(Actor actor);
}
