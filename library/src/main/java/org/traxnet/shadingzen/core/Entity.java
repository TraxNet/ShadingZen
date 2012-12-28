package org.traxnet.shadingzen.core;

import android.util.Log;
import org.traxnet.shadingzen.R;

import java.util.ArrayList;

/** 
 * Root class from which all objects, from 2D sprites to 3D dinos are derived.
 * 
 * ShadingZen tracks the entity list and keeps them updated and draw them in order. 
 * Please refer to Engine.java and EntityManager.java source code for information on Entities' life cycle.
 * 
 * TODO: Give in depth explanation of objects life cycle here
 *
 */
public abstract class Entity {
	private boolean _pendingDestroy;
	private ArrayList<Resource> _resources;
	protected String _nameId;
	private int _lastFrameId;
	
	/** Spawn a new child Actor. 
	 * 
	 * Spawn only instantiates Actors. Please also note that the spawned actor will
	 * be attached to current entity and its life cycle is attached to it. 
	 * 
	 * @param _class Type of Actor we want to instantiate
	 * @param nameId Runtime name for this actor
	 * @return Return a new instance for the given class. 
	 */
	public Actor spawn(Class<? extends Actor> _class, String nameId){
		return Engine.getSharedInstance().getCurrentScene().getEntityManager().spawn(_class, (Actor) this, nameId);
	}
	
	public Entity(){
		_pendingDestroy = false;
		_resources = new ArrayList<Resource>();
	}
	
	/** Registers the new instance. DO NOT CALL THIS METHOD DIRECTLY */
	public void register(String name){
		_nameId = name;
	}
	
	/** Unregister the new instance from the engine and removes all associated resources references.
	 * DO NOT CALL THIS METHOD DIRECTLY
	 */
	public void unregister(){
		for(Resource res : _resources)
			removeResource(res);
	}

	/** Returns the name of this Entity */
	public String getNameId(){
		return _nameId;
	}
	
	/** Sets the latest updated frame id. Used to track if the entity is up to date */
	public void setFrameId(int frameId){
		_lastFrameId = frameId;
	}
	
	/** Returns the updated frame id */
	public int getFrameId(){
		return _lastFrameId;
	}
	
	/** Creates a new resource attached to current Entity
	 * 
	 * If the @see id is found, the same resource is used. This avoid extra memory usage
	 * An initialization object can be passed down to the resource creation. 
	 * 
	 * @param proto Type of resource we want to create
	 * @param id Identification for this resource
	 * @param params Initialization object
	 * @return a new resource of the one present at the resource manager with the same id.
	 */
	protected Resource resourceFactory(Class<? extends Resource> proto, String id, Object params){
		return ResourcesManager.getSharedInstance().factory(proto, (Entity)this, id, R.raw.alpha_tex, params);
	}
	
	/** NEVER CALL THIS DIRECTLY. CALL registerResource INSTEAD */
	public void addResource(Resource res){
		_resources.add(res);
	}
	
	/** NEVER CALL THIS DIRECTLY */
	public void removeResource(Resource res){
		_resources.remove(res);
	}
	
	/** Adds a new reference to the given resource. It safer to use @see  resourceFactory */
	public void registerResource(Resource res){
		addResource(res);
		res.addRef();
	}
	
	/** Removes all associated resource references */
	public void freeResources(){
		for(Resource res : _resources){
			if(null != res)
				res.removeRef();
			else{
				Log.e("ShadingZen", "Trying to remove reference to a null resource");
			}
		}
		
		_resources.clear();
	}
	/*
	
	protected void addRenderTask(RenderTask task){
		EntityManager.getSharedInstance().getRenderService().addRenderTask(task);
	}*/
	
	/** Called during simulation 
	 * 
	 * onTick is called in each simulation loop for each entity
	 * present in the scene. 
	 * 
	 * @param delta The elapsed time between ticks
	 */
	public abstract void onTick(float delta);
	
	/** Draw this entity 
	 * @throws Exception */
	public abstract void onDraw(RenderService renderer) throws Exception;
	
	
	/** Called when loading entity's data */
	public abstract void onLoad();

	/** Called when unloading entity's data */
	public abstract void onUnload();
	
	/** Called when the entity is about to be destroyed
	 * 
	 * At this point, the entity removes all its resources
	 * 
	 * NOTE: Derived classes MUST call super.onDestroy 
	 */
	public void onDestroy(){
		freeResources();
	}
	
	/** Mark this entity to be destroyed
	 * 
	 * Marked entities will not be drawn or processed in each tick
	 */
	public void markForDestroy(){
		_pendingDestroy = true;
	}
	
	/** Returns whenever the engine should remove the entity as soon as possible */
	public boolean isPendingDestroy(){
		return _pendingDestroy;
	}
	
	
}
