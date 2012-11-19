package org.traxnet.shadingzen.core;

import java.util.ArrayList;
import org.traxnet.shadingzen.R;
import android.util.Log;

public abstract class Entity {
	protected boolean _pendingDestroy;
	protected ArrayList<Resource> _resources;
	protected String _nameId;
	protected int _lastFrameId;
	
	public Actor spawn(Class<? extends Actor> _class, String nameId){
		return Engine.getSharedInstance().getCurrentScene().getEntityManager().spawn(_class, (Actor) this, nameId);
	}
	
	public Entity(){
		_pendingDestroy = false;
		_resources = new ArrayList<Resource>();
	}
	
	public void register(String name){
		_nameId = name;
	}
	
	public void unregister(){
		for(Resource res : _resources)
			removeResource(res);
	}

	public String getNameId(){
		return _nameId;
	}
	
	public void setFrameId(int frameId){
		_lastFrameId = frameId;
	}
	
	public int getFrameId(){
		return _lastFrameId;
	}
	
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
	
	public void registerResource(Resource res){
		addResource(res);
		res.addRef();
	}
	
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
	
	public boolean isPendingDestroy(){
		return _pendingDestroy;
	}
	
	
}
