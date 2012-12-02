package org.traxnet.shadingzen.rendertask;

import java.util.Hashtable;

import org.traxnet.shadingzen.util.ObjectPool;

import android.util.Log;

public final class RenderTaskPool {
	private static RenderTaskPool _sharedInstance = null;
	
	private static int MAX_POOL_SIZE = 50;
	
	public static synchronized RenderTaskPool sharedInstance(){
		if(null == _sharedInstance){
			_sharedInstance = new RenderTaskPool();
		}
		
		return _sharedInstance;
	}
	
	private Hashtable<Class<? extends RenderTask>, ObjectPool> _renderTasksPools;
	
	private RenderTaskPool(){
		_renderTasksPools = new Hashtable<Class<? extends RenderTask>, ObjectPool>();
	}
	
	public synchronized RenderTask newTask(Class<? extends RenderTask> _class){
		ObjectPool pool = null;
		if(!_renderTasksPools.containsKey(_class)){
			pool = new ObjectPool(MAX_POOL_SIZE);
			_renderTasksPools.put(_class, pool);
		} else{
			pool = _renderTasksPools.get(_class);
		}
		
		try {
			return (RenderTask) pool.newObject(_class);
		} catch (InstantiationException e) {
			Log.e("ShadingZen", "Unable to create RenderTask from pool", e);
			return null;
		} catch (IllegalAccessException e) {
			Log.e("ShadingZen", "Unable to create RenderTask from pool", e);
			return null;
		}
	}
	
	public synchronized void freeTask(RenderTask task){
		Class<? extends RenderTask> _class = task.getClass();
		if(!_renderTasksPools.containsKey(_class)){
			Log.e("ShadingZen", "Freeing an unknow type of RenderTask:" + _class.getName());
			return;
		}
		
		ObjectPool pool = _renderTasksPools.get(_class);
		pool.freeObject(task);
	}
}
