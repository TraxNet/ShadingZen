package org.traxnet.shadingzen.core;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public final class EntityManager {
	//private static EntityManager _global_instance;
	
	public class EntityHolder {
		final Entity _entity;
		boolean _is_marked;
		public EntityHolder(Entity entity){
			_entity = entity;
			_is_marked = false;
		}
		 
		public Entity getEntity(){
			return _entity;
		}
		 
		public void mark(){
			_is_marked = true;
		}
		 
		public boolean isMarked(){
			return _is_marked;
		}
		
	}
	
	HashMap<Entity, EntityHolder> _entities;
	
	/*
	public static void setSharedInstance(EntityManager manager) throws IllegalAccessException{
		//if(null != _global_instance) throw new IllegalAccessException();
		
		_global_instance = manager;
	}
	
	public static EntityManager getSharedInstance(){
		return _global_instance;
	}*/
	
	
	
	public EntityManager(Scene owner){
		_entities = new HashMap<Entity, EntityHolder>();
		
		//_ownerScene = owner;
	}
	
	public void unloadEntityResources(){
		for(EntityHolder holder : _entities.values()){
			holder.getEntity().onUnload();
		}
	}
	
	public void reloadEntityResources(){
		for(EntityHolder holder : _entities.values()){
			holder.getEntity().onLoad();
		}
	}
	
	/*
	public synchronized Entity spawn(Class<? extends Actor> _class, Actor parent){
		return spawn(_class, parent, UUID.randomUUID().toString());
	}
	*/
	public void removeAllEntities(){
		synchronized(this._entities){
			for(EntityHolder holder : _entities.values()){
				holder.getEntity().onDestroy();
			}
			
			_entities.clear();
		}
	}
	
	public Actor spawn(Class<? extends Actor> _class, Actor parent, String nameId){
		synchronized(this._entities){
			Entity entity = newEntity(_class, nameId);
			if(null == entity)
				return null;
			Actor actor = (Actor)entity;
			
			if(null != parent){
				parent.addChildren(actor);
				actor.setParent(parent);
			}
			
			return actor;
		}
	}
	
	public synchronized Entity newEntity(Class<? extends Entity> _class, String nameId){
		Entity entity = null;
		try {
			entity = _class.newInstance();
			entity.register(nameId);
			
			_entities.put(entity, new EntityHolder(entity));

            entity.onLoad();
	
		} catch (Exception e){
			Log.e("ShadingZen", "Error spawning class <" + _class.toString() + ">:" + e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
			Log.e("ShadingZen", sw.toString());
		}
		
		return entity;
	}
	
	public Object[] getCurrentEntityHolders(){
		Object [] holders = _entities.values().toArray();
		return holders;
	}
	
	/*
	public synchronized void destroy(Entity entity){
		_entities.get(entity).mark();
	}*/
	
	public void updateTick(float deltatime){
		deletePendingEntities();
		
		synchronized(this._entities){
			Object [] holders = _entities.values().toArray();
			for(Object obj : holders){
				EntityHolder holder = (EntityHolder)obj;
				if(!holder.getEntity().isPendingDestroy())
					holder.getEntity().onTick(deltatime);
				/*else{
					Log.v("ShadingZen", "Removing entity " + holder.getEntity().getNameId());
					_entities.remove(holder.getEntity());
					holder.getEntity().onDestroy();
				}*/
			}
		}
	}
	
	public void deletePendingEntities(){
		synchronized(this._entities){
			Object [] holders = _entities.values().toArray();
			for(Object obj : holders){
				EntityHolder holder = (EntityHolder)obj;
				if(holder.getEntity().isPendingDestroy()){
					Log.v("ShadingZen", "Removing entity " + holder.getEntity().getNameId());
					_entities.remove(holder.getEntity());
					holder.getEntity().onDestroy();
				}
			}
		}
	}
	
	
	
}
