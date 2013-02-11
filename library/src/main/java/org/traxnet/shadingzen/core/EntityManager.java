package org.traxnet.shadingzen.core;

import android.opengl.Matrix;
import android.util.Log;
import org.traxnet.shadingzen.math.Vector3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public final class EntityManager {
	Scene _ownerScene;
	
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

    protected LinkedList<LightEmitter> _lightEmitters;
	HashMap<Entity, EntityHolder> _entities;
    Object [] _entitiesCache;
    int _numEntitiesCached = 0;
    boolean _needCacheUpdate = false;
	
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

        _lightEmitters = new LinkedList<LightEmitter>();
		_ownerScene = owner;
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

    protected void addLightEmitter(Entity entity){
        if(LightEmitter.class.isAssignableFrom(entity.getClass()))
            _lightEmitters.add((LightEmitter)entity);
    }

    protected void removeLightEmitter(Entity entity){
        if(LightEmitter.class.isAssignableFrom(entity.getClass()))
            _lightEmitters.remove(entity);
    }

    public LightEmitter getFirstLightEmitter(){
        return _lightEmitters.get(0);
    }

    Vector<LightEmitterSortingWarper> createLightEmitterSortingWarpersList(Vector3 point){
        Vector<LightEmitterSortingWarper> warpers = new Vector<LightEmitterSortingWarper>(_lightEmitters.size());

        for(LightEmitter emitter : _lightEmitters){
            LightEmitterSortingWarper warper = new LightEmitterSortingWarper();
            warper._emitter = emitter;
            warper._point = point;
            warpers.add(warper);
        }

        return warpers;
    }

    class LightEmitterSortingWarper implements Comparable<LightEmitterSortingWarper>{
        public LightEmitter _emitter;
        public Vector3 _point;

        boolean cached = false;
        float cached_value = 0.f;

        public float getCachedIntesityValue(){
            if(!cached)
                cached_value = _emitter.computeContributionAtWorldPoint(_point.x, _point.y, _point.z);
            return cached_value;
        }

        @Override
        public int compareTo(LightEmitterSortingWarper emitter) {


            if(cached_value < emitter.getCachedIntesityValue()) return -1;
            if(cached_value > emitter.getCachedIntesityValue()) return 1;
            return 0;
        }
    }



    public LightEmitter[] getLightEmittersByContrubtionAtPoint(Vector3 point){
        LightEmitter[] list = new LightEmitter[_lightEmitters.size()];
        int count = 0;
        Vector<LightEmitterSortingWarper> warpers = createLightEmitterSortingWarpersList(point);

        Collections.sort(warpers);

        int index = 0;
        for(index = 0; index < warpers.size(); index++){
            list[index] = warpers.get(index)._emitter;
        }

        return list;
    }
	
	public synchronized Entity newEntity(Class<? extends Entity> _class, String nameId){
		Entity entity = null;
		try {
			entity = _class.newInstance();
			entity.register(nameId);
			
			_entities.put(entity, new EntityHolder(entity));

            if(Actor.class.isAssignableFrom(_class))
                ((Actor)entity).setOwnerScene(_ownerScene);

            _needCacheUpdate = true;

            addLightEmitter(entity);

            entity.onLoad();

            if(Collider.class.isAssignableFrom(_class))
                _ownerScene.registerCollider((Collider)entity);
	
		} catch (Exception e){
			Log.e("ShadingZen", "Error spawning class <" + _class.toString() + ">:" + e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
			Log.e("ShadingZen", sw.toString());
		}
		
		return entity;
	}
	
	public final Object[] getCurrentEntityHolders(){
        synchronized(this._entities){
            if(_needCacheUpdate){
                _entitiesCache = _entities.values().toArray();
                _needCacheUpdate = false;
                _numEntitiesCached = _entities.size();
            }
            return _entitiesCache;
        }
	}
	
	/*
	public synchronized void destroy(Entity entity){
		_entities.get(entity).mark();
	}*/
	
	public void updateTick(float deltatime, float [] scene_model_matrix){
		deletePendingEntities();
		
		//synchronized(this._entities){
			Object [] holders = getCurrentEntityHolders();
			for(int index=0; index < _numEntitiesCached; index++){

				EntityHolder holder = (EntityHolder)_entitiesCache[index];
                Entity entity = holder.getEntity();
				if(entity.isPendingDestroy())
                    continue;

                if(Actor.class.isInstance(entity))
                {
                    Actor actor = (Actor) entity;
                    if(null != actor.getParent() && actor.getParent() != _ownerScene)
                        continue;

                    updateActor((Actor) entity, deltatime, scene_model_matrix);
                } else{
			        entity.onTick(deltatime);
                }

			}
		//}
	}

    private void updateActor(Actor actor, float delta_time, float[] parent_model_matrix) {
        Matrix.multiplyMM(actor.getWorldModelMatrix().getAsArray(), 0, parent_model_matrix, 0, actor.getLocalModelMatrix().getAsArray(), 0);
        Matrix.invertM(actor.getInverseWorldModelMatrix().getAsArray(), 0, actor.getWorldModelMatrix().getAsArray(), 0);


        actor.onTick(delta_time);


        Object [] children = actor.getChildren();
        int size = actor.getNumChildren();
        for(int index=0; index < size; index++){
            Actor child = ((Actor) children[index]);
            updateActor(child, delta_time, actor.getWorldModelMatrix().getAsArray());
            //child.setFrameId(_currentFrameId);
        }
    }

    public void deletePendingEntities(){
		synchronized(this._entities){

            for(int index=0; index < _numEntitiesCached; index++){
				EntityHolder holder = (EntityHolder)_entitiesCache[index];
				if(holder.getEntity().isPendingDestroy()){
					Log.v("ShadingZen", "Removing entity " + holder.getEntity().getNameId());
					_entities.remove(holder.getEntity());
                    removeLightEmitter(holder.getEntity());
					holder.getEntity().onDestroy();

                    _needCacheUpdate = true;
				}
			}
		}
	}
	
	
	
}
