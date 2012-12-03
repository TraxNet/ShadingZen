package org.traxnet.shadingzen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

public class Scene extends Actor{
	protected EntityManager _entityManager;
	//protected GameInfo _currentGameInfo;
	protected HashMap<String, Collider> _colliders;
	
	
	
	public Scene(){
		_entityManager = new EntityManager(this);
		_colliders = new HashMap<String, Collider>();
	}
	
	public EntityManager getEntityManager(){
		return _entityManager;
	}
	
	/** Spawns a new Actor an make it child of the given parent Actor 
	 * 
	 * @param _class The type of Actor we want to spawn
	 * @param parent The parent of the newly created Actor
	 * @param nameId The ID 
	 * @return a new instance of the given Actor class
	 */
	public synchronized Actor spawn(Class<? extends Actor> _class, Actor parent, String nameId){
		return _entityManager.spawn(_class, parent, nameId);
	}
	
	public  void registerCollider(Collider reg){
		synchronized(_colliders){
			_colliders.put(reg.getNameId(), reg);
		}
	}
	
	public synchronized void unregisterCollider(Collider reg){
		synchronized(_colliders){
			if(_colliders.containsKey(reg.getNameId()))
				_colliders.remove(reg.getNameId());
		}
	}
	
	public void processColliders(){
		ArrayList<Collider> processed = new ArrayList<Collider>();
		for(Collider collider : _colliders.values()){
			processed.add(collider);
			
			for(Collider target : _colliders.values()){
				if(processed.contains(target))
					continue;
				if(collider.getBoundingBox().overlap(target.getBoundingBox())){
					// They both may collide. 
					// TODO: for now we only test bbox for speed and simplicity
					collider.onTouch(target);
					target.onTouch(collider);
				}
						
			}
		}
	}
	
	public class ColliderRayTestResult implements Comparable {
		Vector3 _point;
		Collider _collider;
		float _length;
		
		public ColliderRayTestResult(Vector3 point, Collider collider, float length){
			_point = point;
			_collider = collider;
			_length = length;
		}
		
		public Collider getCollider(){
			return _collider;
		}
		
		public Vector3 getPoint(){
			return _point;
		}

		@Override
		public int compareTo(Object another) {
			// TODO Auto-generated method stub
			ColliderRayTestResult other_result = (ColliderRayTestResult) another;
			if(_length > other_result._length)
				return 1;
			else if(_length < other_result._length)
				return -1;
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ColliderRayTestResult getColliderAlongRay(Vector3 ray_orig, Vector3 ray_dir){
		ArrayList<ColliderRayTestResult> list = new ArrayList<ColliderRayTestResult>();
		
		for(Collider collider : _colliders.values()){
			BBox box = collider.getBoundingBox();
			Vector3 coord = new Vector3();
			
			if(!box.testRay(ray_orig, ray_dir, new AtomicReference<Vector3>(coord)))
			//if(!box.testRayWithSphereExtents(ray_orig, ray_dir))
				continue;
			
			Vector3 pos = coord.sub(ray_orig);
			
			list.add(new ColliderRayTestResult(coord, collider, pos.lengthSqrt()));
			
		}
		if(list.size() <= 0)
			return null;
		
		Collections.sort(list);
		return list.remove(0);
	}

	@Override
	public void onTick(float delta) {
		super.onTick(delta);
		_entityManager.updateTick(delta);
	}

	@Override
	public void onDraw(RenderService renderer) {
		//_entityManager.drawHierarchy(renderer);
		
	}

	@Override
	public void onLoad() {
		_entityManager.reloadEntityResources();
	}

	@Override
	public void onUnload() {
		_entityManager.unloadEntityResources();
	}
	
	@Override
	public void onDestroy(){
		_entityManager.removeAllEntities();
	}

	@Override
	protected void onUpdate(float deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	
}
