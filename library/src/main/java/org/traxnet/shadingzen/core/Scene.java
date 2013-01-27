package org.traxnet.shadingzen.core;

import android.opengl.Matrix;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class Scene extends Actor{
	protected EntityManager _entityManager;
	//protected GameInfo _currentGameInfo;
	protected HashMap<String, Collider> _colliders;
    protected Object []_collidersCache;
    protected boolean _needsCacheUpdate = false;
    protected int _numCachedColliders = 0;

	
	
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

    @Override
    public Actor spawn(Class<? extends Actor> _class, String nameId){
        return _entityManager.spawn(_class, this, nameId);
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

    float [] _temp_previous_position = new float[4];
    float [] _temp_ray_origin_targetspace = new float[4];
    Vector3 _temp_dir = new Vector3();
    Vector3 _temp_ray_origin_vec = new Vector3();
    Vector3 _temp_distance_vec = new Vector3();
    float [] _temp_dir_as_array = new float[4];
    float [] _temp_dir_as_array_targetspace = new float[4];
    CollisionInfo info = new CollisionInfo();
    Vector<CollisionInfo> detected_collisions = new Vector<CollisionInfo>();

    public void processColliders(){
        //ArrayList<Collider> processed = new ArrayList<Collider>();
        synchronized (_colliders){
            if(_needsCacheUpdate){
                _collidersCache = _colliders.values().toArray();
                _needsCacheUpdate = false;
                _numCachedColliders = _collidersCache.length;
            }
        }

        info.hitActor = null;
        info.hitNormal.set(0.f, 0.f, 0.f);
        info.hitPoint.set(0.f, 0.f, 0.f);
        AtomicReference ref = new AtomicReference<Vector3>();

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

            checkCollidersAlongRayPathInTargetLocalSpace(true, detected_collisions, ref, collider, distance_traveled, _temp_previous_position, _temp_dir_as_array, collider.getBoundingRadius());
        }
    }

    private void checkCollidersAlongRayPathInTargetLocalSpace(boolean callOnTouch, Vector<CollisionInfo> detected_collisions, AtomicReference ref, Collider collider, float ray_length, float[] ray_origin, float[] ray_dir, float ray_radius) {
        for(int target_index = 0; target_index < _numCachedColliders; target_index++){
            Collider target = (Collider) _collidersCache[target_index];

            if(collider == target)
                continue;

            /*if(processed.contains(target))
                continue; */
            if(target.isPendingDestroy())
                continue;
            if(target.getCollidableStatus() == Collider.CollidableStatus.DISABLED)
                continue;

            float [] target_inv_model_matrix = target.getInverseWorldModelMatrix().getAsArray();

            Matrix.multiplyMV(_temp_ray_origin_targetspace, 0, target_inv_model_matrix, 0, ray_origin, 0);
            _temp_ray_origin_vec.x = _temp_ray_origin_targetspace[0];
            _temp_ray_origin_vec.y = _temp_ray_origin_targetspace[1];
            _temp_ray_origin_vec.z = _temp_ray_origin_targetspace[2];
            //Vector3 position_in_targetspace = target.getInverseWorldModelMatrix().mul(collider.getPreviousPosition());
            BBox target_bbox = target.getBoundingBox();

            float distance_from_target = _temp_ray_origin_vec.lengthSqrt();
            if(distance_from_target > target.getBoundingRadius() + ray_radius + ray_length)
                continue;

            /*if(target_bbox.isPointInside(position_in_targetspace, target.getBoundingRadius())){
                info.hitActor = target;

                if(collider.onTouch(info))
                    break;
                else
                    continue;
            } */


            Matrix.multiplyMV(_temp_dir_as_array_targetspace, 0, target_inv_model_matrix, 0, ray_dir, 0);
           /* _temp_dir.x = _temp_dir_as_array_targetspace[0];
            _temp_dir.y = _temp_dir_as_array_targetspace[1];
            _temp_dir.z = _temp_dir_as_array_targetspace[2];   */

            /*
            Vector4 rot_dir = new Vector4(_temp_dir, 0.f);
            Vector4 rot_dir2 = target.getInverseWorldModelMatrix().mul(rot_dir);
            _temp_dir.set(rot_dir2.x, rot_dir2.y, rot_dir2.z);  */

            if(ray_length >= 0.f){
                // _temp_dir.normalizeNoCopy();

                ref.set(info.hitPoint);
                if(target_bbox.testRay(_temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, collider.getBoundingRadius(), ref)){
                    _temp_distance_vec.set(info.hitPoint);
                    _temp_distance_vec.sub(_temp_ray_origin_vec);
                    //Vector3 distance = info.hitPoint.sub(_temp_ray_origin_vec);


                    float distance_from_hit = _temp_distance_vec.lengthSqrt();
                    if(ray_length >= distance_from_hit){
                        info = target.checkForRayIntersection(info, _temp_ray_origin_targetspace, _temp_dir_as_array_targetspace, collider.getBoundingRadius(), ray_length);
                        info.hitActor = target;
                        info.hitLength = distance_from_hit;

                        //Log.i("ShadingZen", "Collision Detected collider="+collider.getNameId() + " target="+target.getNameId());

                        if(callOnTouch && collider.onTouch(info))
                            break;
                        else if(!callOnTouch){
                            CollisionInfo info_clone = new CollisionInfo();
                            info_clone.hitActor = info.hitActor;
                            info_clone.hitPoint.set(info.hitPoint);
                            info_clone.hitNormal.set(info.hitNormal);
                            info_clone.hitLength = info.hitLength;

                            detected_collisions.add(info_clone);

                            continue;
                        }
                    }
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

    public CollisionInfo getNearestColliderAlongRay(float [] orig, float [] dir, float length, float radius){
         return null;
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
		// TODO Auto-generated method stub
		
	}
	
	
}
