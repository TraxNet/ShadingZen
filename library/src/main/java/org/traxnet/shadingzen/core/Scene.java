package org.traxnet.shadingzen.core;

import android.opengl.Matrix;
import org.traxnet.shadingzen.core.queries.ActorQuery;
import org.traxnet.shadingzen.core.queries.ActorQueryByClass;
import org.traxnet.shadingzen.core.queries.ActorQueryResultCallback;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class Scene extends Actor{
	protected EntityManager _entityManager;
	//protected GameInfo _currentGameInfo;
	protected HashMap<String, Collider> _colliders;
    protected Object []_collidersCache;
    protected boolean _needsCacheUpdate = false;
    protected int _numCachedColliders = 0;

    float [] _temp_previous_position = new float[4];
    float [] _temp_ray_origin_targetspace = new float[4];
    Vector3 _temp_dir = new Vector3();
    Vector3 _temp_ray_origin_vec = new Vector3();
    Vector3 _temp_distance_vec = new Vector3();
    float [] _temp_dir_as_array = new float[4];
    float [] _temp_dir_as_array_targetspace = new float[4];
    //CollisionInfo info = new CollisionInfo();
    static int  MAX_DETECTABLE_COLLISIONS = 20;
    CollisionInfo [] _temp_detected_collisions = new CollisionInfo[MAX_DETECTABLE_COLLISIONS];

    ActorQueryPool actorQueryPool = new ActorQueryPool();
	
	public Scene(){
		_entityManager = new EntityManager(this);
		_colliders = new HashMap<String, Collider>();

        initializeTempDetectedCollisionsArray();
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
		Actor actor = _entityManager.spawn(_class, parent, nameId);
        actor.setOwnerScene(this);
        return actor;
	}

    @Override
    public Actor spawn(Class<? extends Actor> _class, String nameId){
        Actor actor = _entityManager.spawn(_class, this, nameId);
        actor.setOwnerScene(this);
        return actor;
    }
	
	public  void registerCollider(Collider reg){
		synchronized(_colliders){
			_colliders.put(reg.getNameId(), reg);
            _needsCacheUpdate = true;
		}
	}
	
	public synchronized void unregisterCollider(Collider reg){
		synchronized(_colliders){
			if(_colliders.containsKey(reg.getNameId())) {
				_colliders.remove(reg.getNameId());
                _needsCacheUpdate = true;
            }
		}
	}



    void initializeTempDetectedCollisionsArray(){
        for(int i=0; i < MAX_DETECTABLE_COLLISIONS; i++){
            _temp_detected_collisions[i] = new CollisionInfo();
        }
    }

    public void processColliders(){
        synchronized (_colliders){
            if(_needsCacheUpdate){
                _collidersCache = _colliders.values().toArray();
                _needsCacheUpdate = false;
                _numCachedColliders = _collidersCache.length;
            }
        }


        for(int collider_index = 0; collider_index < _numCachedColliders; collider_index++){
            Collider collider = (Collider) _collidersCache[collider_index];
            //processed.add(collider);

            if(collider.isPendingDestroy())
                continue;

            Collider.CollidableStatus status = collider.getCollidableStatus();
            if(status == Collider.CollidableStatus.DISABLED || status == Collider.CollidableStatus.COLLIDABLE_BY_OTHERS)
                continue;

            Vector3 pre_pos_vec = collider.getPreviousPosition();
            _temp_previous_position[0] = pre_pos_vec.x;
            _temp_previous_position[1] = pre_pos_vec.y;
            _temp_previous_position[2] = pre_pos_vec.z;
            _temp_previous_position[3] = 1.f;


            _temp_dir.set(collider.getPosition());
            _temp_dir.subNoCopy(pre_pos_vec);

            float distance_traveled = _temp_dir.lengthSqrt();
            if(distance_traveled >= 0.000001f){
                _temp_dir.x /= distance_traveled;
                _temp_dir.y /= distance_traveled;
                _temp_dir.z /= distance_traveled;
            } else{
                _temp_dir.set(0.f, 0.f, 0.f);
            }

            _temp_dir_as_array[0] = _temp_dir.x;
            _temp_dir_as_array[1] = _temp_dir.y;
            _temp_dir_as_array[2] = _temp_dir.z;

            int num_detected_collisions = checkCollidersAlongRayPathInTargetLocalSpace(collider, _temp_detected_collisions, MAX_DETECTABLE_COLLISIONS, distance_traveled, _temp_previous_position, _temp_dir_as_array, collider.getBoundingRadius());

            for(int index=0; index < Math.min(MAX_DETECTABLE_COLLISIONS, num_detected_collisions); index++){
                if(collider.onTouch(_temp_detected_collisions[index]))
                    break;
            }
        }
    }

    private int checkCollidersAlongRayPathInTargetLocalSpace(Collider collider, CollisionInfo[] detected_collisions, int detected_collisions_size, float ray_length, float[] ray_origin, float[] ray_dir, float ray_radius) {
        int num_detected_collisions = 0;
        for(int target_index = 0; target_index < _numCachedColliders; target_index++){
            Collider target = (Collider) _collidersCache[target_index];

            if(null != collider && collider == target)
                continue;

            if(target.isPendingDestroy())
                continue;
            if(target.getCollidableStatus() == Collider.CollidableStatus.DISABLED)
                continue;

            float [] target_inv_model_matrix = target.getInverseWorldModelMatrix().getAsArray();

            Matrix.multiplyMV(_temp_ray_origin_targetspace, 0, target_inv_model_matrix, 0, ray_origin, 0);
            _temp_ray_origin_vec.x = _temp_ray_origin_targetspace[0];
            _temp_ray_origin_vec.y = _temp_ray_origin_targetspace[1];
            _temp_ray_origin_vec.z = _temp_ray_origin_targetspace[2];

            BBox target_bbox = target.getBoundingBox();

            float distance_from_target = _temp_ray_origin_vec.lengthSqrt();
            if(distance_from_target > target.getBoundingRadius() + ray_radius + ray_length)
                continue;


            Matrix.multiplyMV(_temp_dir_as_array_targetspace, 0, target_inv_model_matrix, 0, ray_dir, 0);

            if(ray_length >= 0.f){
                CollisionInfo info = detected_collisions[num_detected_collisions];
                info.setZeros();

                if(target_bbox.testRay(_temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, ray_radius, info.hitPoint)){
                    _temp_distance_vec.set(info.hitPoint);
                    _temp_distance_vec.sub(_temp_ray_origin_vec);


                    float distance_from_hit = _temp_distance_vec.lengthSqrt();
                    if(ray_length >= distance_from_hit){

                        info = target.checkForRayIntersection(info, _temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, ray_radius, ray_length);
                        if(null == info)
                            continue;

                        info.hitActor = target;
                        info.hitLength = distance_from_hit;

                        num_detected_collisions++;

                        //Log.i("ShadingZen", "Collision Detected collider="+collider.getNameId() + " target="+target.getNameId());

                        if(num_detected_collisions >= detected_collisions_size)
                            break;
                    }
                }
            }
        }

        return num_detected_collisions;
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

    public CollisionInfo getNearestColliderAlongRay(Collider exclude, float [] orig, float [] dir, float length, float radius){
        int num_collision = checkCollidersAlongRayPathInTargetLocalSpace(exclude, _temp_detected_collisions, MAX_DETECTABLE_COLLISIONS, length, orig, dir, radius);

        if(0 == num_collision)
            return null;

        Vector<CollisionInfo> sorted_list = new Vector<CollisionInfo>();
        for(int index=0; index < Math.min(MAX_DETECTABLE_COLLISIONS, num_collision); index++)
            sorted_list.add(_temp_detected_collisions[index]);


        Collections.sort(sorted_list);

        return sorted_list.get(0);
    }

    /*
	public ColliderRayTestResult getColliderAlongRay(Vector3 ray_orig, Vector3 ray_dir){
		ArrayList<ColliderRayTestResult> list = new ArrayList<ColliderRayTestResult>();
		
		for(Collider collider : _colliders.values()){
			BBox box = collider.getBoundingBox();
			Vector3 coord = new Vector3();
			
			if(!box.testRay(ray_orig, ray_dir, 0.f, new AtomicReference<Vector3>(coord)))
			//if(!box.testRayWithSphereExtents(ray_orig, ray_dir))
				continue;
			
			Vector3 pos = coord.sub(ray_orig);
			
			list.add(new ColliderRayTestResult(coord, collider, pos.lengthSqrt()));
			
		}
		if(list.size() <= 0)
			return null;
		
		Collections.sort(list);
		return list.remove(0);
	}   */

	@Override
	public void onTick(float delta) {
		super.onTick(delta);
		_entityManager.updateTick(delta, getLocalModelMatrix().getAsArray());
        processColliders();
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

	}
	

    public void runActorQuery(ActorQuery query, ActorQueryResultCallback callback){
        final Object [] holders = _entityManager.getCurrentEntityHolders();

        if(null == holders)
            return;

        for(int index=0; index < holders.length; index++){
            EntityManager.EntityHolder holder = (EntityManager.EntityHolder) holders[index];

            if(!Actor.class.isInstance(holder.getEntity()))
                continue;

            Actor actor = (Actor) holder.getEntity();
             if(query.testActor(actor))
                 callback.onActorFound(actor);
        }

        actorQueryPool.freeQuery(query);
    }

    public ActorQueryByClass newQueryByClass(){
        return (ActorQueryByClass) actorQueryPool.newQuery(ActorQueryByClass.class);
    }

    public ActorQuery newActorQuery(Class<? extends ActorQuery> queryClass){
        return actorQueryPool.newQuery(queryClass);
    }
}
