package org.traxnet.shadingzen.core.queries;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.math.BBox;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 6:45
 */
public class ActorQueryByClass extends ActorQuery {
    Class<? extends Actor> _class;

    public void setSearchClass(Class<? extends Actor> searchClass){
        _class = searchClass;
    }

    @Override
    public boolean checkIntersectsAABBox(BBox box) {
        return true;
    }

    @Override
    public boolean testActor(Actor actor) {
        if(_class.isInstance(actor))
            return true;
        return false;
    }

    @Override
    public void initializeFromPool() {
        _class = null;
    }

    @Override
    public void finalizeFromPool() {

    }
}
