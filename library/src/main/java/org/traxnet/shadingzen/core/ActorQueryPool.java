package org.traxnet.shadingzen.core;

import android.util.Log;
import org.traxnet.shadingzen.core.queries.ActorQuery;
import org.traxnet.shadingzen.util.ObjectPool;

import java.util.Hashtable;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 6/02/13
 * Time: 6:36
 */
final class ActorQueryPool{


    private static int MAX_POOL_SIZE = 50;


    private Hashtable<Class<? extends ActorQuery>, ObjectPool> _queryPools;

    public ActorQueryPool(){
        _queryPools = new Hashtable<Class<? extends ActorQuery>, ObjectPool>();
    }

    public synchronized ActorQuery newQuery(Class<? extends ActorQuery> _class){
        ObjectPool pool = null;
        if(!_queryPools.containsKey(_class)){
            pool = new ObjectPool(MAX_POOL_SIZE);
            _queryPools.put(_class, pool);
        } else{
            pool = _queryPools.get(_class);
        }

        try {
            return (ActorQuery) pool.newObject(_class);
        } catch (InstantiationException e) {
            Log.e("ShadingZen", "Unable to create RenderTask from pool:" + e.getLocalizedMessage(), e);
            return null;
        } catch (IllegalAccessException e) {
            Log.e("ShadingZen", "Unable to create RenderTask from pool:" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public synchronized void freeQuery(ActorQuery task){
        Class<? extends ActorQuery> _class = task.getClass();
        if(!_queryPools.containsKey(_class)){
            Log.e("ShadingZen", "Freeing an unknown type of ActorQuery object:" + _class.getName());
            return;
        }

        ObjectPool pool = _queryPools.get(_class);
        pool.freeObject(task);
    }
}
