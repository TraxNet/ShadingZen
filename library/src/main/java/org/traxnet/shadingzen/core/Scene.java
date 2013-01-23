package org.traxnet.shadingzen.core;

import android.opengl.Matrix;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

import java.util.HashMap;
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

    float [] previous_position = new float[4];
    float [] previous_position_targetspace = new float[4];
    Vector3 dir = new Vector3();
    Vector3 previous_position_vec = new Vector3();
    Vector3 distance_vec = new Vector3();
    float [] dir_as_array = new float[4];
    float [] dir_as_array_targetspace = new float[4];
    CollisionInfo info = new CollisionInfo();

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
            previous_position[0] = pre_pos_vec.x;
            previous_position[1] = pre_pos_vec.y;
            previous_position[2] = pre_pos_vec.z;
            previous_position[3] = 1.f;


            dir.set(collider.getPosition());
            dir.subNoCopy(pre_pos_vec);

            float distance_traveled = dir.lengthSqrt();
            if(distance_traveled >= 0.000001f){
                dir.x /= distance_traveled;
                dir.y /= distance_traveled;
                dir.z /= distance_traveled;
            } else{
                dir.set(0.f, 0.f, 0.f);
            }

            dir_as_array[0] = dir.x;
            dir_as_array[1] = dir.y;
            dir_as_array[2] = dir.z;

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

                Matrix.multiplyMV(previous_position_targetspace, 0, target_inv_model_matrix, 0, previous_position, 0);
                previous_position_vec.x = previous_position_targetspace[0];
                previous_position_vec.y = previous_position_targetspace[1];
                previous_position_vec.z = previous_position_targetspace[2];
                //Vector3 position_in_targetspace = target.getInverseWorldModelMatrix().mul(collider.getPreviousPosition());
                BBox target_bbox = target.getBoundingBox();

                float distance_from_target = previous_position_vec.lengthSqrt();
                if(distance_from_target > target.getBoundingRadius() + collider.getBoundingRadius() + distance_traveled)
                    continue;

                /*if(target_bbox.isPointInside(position_in_targetspace, target.getBoundingRadius())){
                    info.hitActor = target;

                    if(collider.onTouch(info))
                        break;
                    else
                        continue;
                } */


                Matrix.multiplyMV(dir_as_array_targetspace, 0, target_inv_model_matrix, 0, dir_as_array, 0);
                dir.x = dir_as_array_targetspace[0];
                dir.y = dir_as_array_targetspace[1];
                dir.z = dir_as_array_targetspace[2];

                /*
                Vector4 rot_dir = new Vector4(dir, 0.f);
                Vector4 rot_dir2 = target.getInverseWorldModelMatrix().mul(rot_dir);
                dir.set(rot_dir2.x, rot_dir2.y, rot_dir2.z);  */

                if(distance_traveled > 0.f){
                    // dir.normalizeNoCopy();

                    ref.set(info.hitPoint);
                    if(target_bbox.testRay(previous_position_targetspace, dir_as_array_targetspace, collider.getBoundingRadius(), ref)){
                        distance_vec.set(info.hitPoint);
                        distance_vec.sub(previous_position_vec);
                        //Vector3 distance = info.hitPoint.sub(previous_position_vec);


                        float distance_from_hit = distance_vec.lengthSqrt();
                        if(distance_traveled >= distance_from_hit){
                            info = target.checkForRayIntersection(info, previous_position_targetspace, dir_as_array_targetspace, collider.getBoundingRadius(), distance_traveled);
                            info.hitActor = target;

                            //Log.i("ShadingZen", "Collision Detected collider="+collider.getNameId() + " target="+target.getNameId());

                            if(collider.onTouch(info))
                                break;
                            else
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
		_entityManager.updateTick(delta);
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
