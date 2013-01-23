package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Vector3;

public abstract class Collider extends Actor {
	protected Vector3 _extends;
    protected CollidableStatus _collidableStatus = CollidableStatus.FULL_COLLIDABLE;

    public enum CollidableStatus{
        FULL_COLLIDABLE,
        COLLIDABLE_BY_OTHERS,
        DISABLED
    }


    public CollidableStatus getCollidableStatus(){
        return _collidableStatus;
    }

    public void setCollidableStatus(CollidableStatus status){
        _collidableStatus = status;
    }

	public Collider(){

	}



	
	@Override
	public void register(String name){
		super.register(name);
		
		Engine.getSharedInstance().getCurrentScene().registerCollider(this);
	}
	
	@Override
	public void unregister(){
		// Un-register from the collider manager
		Engine.getSharedInstance().getCurrentScene().unregisterCollider(this);
		
		super.unregister();
	}

    public abstract float getBoundingRadius();

    /**
     * Should check whenever the given ray (origin, dir, radius, length) intersects this actor
     * If an intersection is found a CollisionInfo object is returned. If no collision is found
     * a null object must be returned
     * @param info Collision info. It may contain previous info, must be returned filled if an intersection is found
     * @param orig Ray origin
     * @param dir Ray direction
     * @param radius Ray simulated radius
     * @param length Ray length
     * @return A collisionInfo object if the ray intersects, or null otherwise
     *
     */
    public abstract CollisionInfo checkForRayIntersection(CollisionInfo info, float [] orig, float [] dir, float radius, float length);

    /**
     * The engine's runtime will call this method whenever a collision with other actor occurs.
     *
     * @param info
     * @return If returns is false, the engine will continue probing for more collisions
     */
	public abstract boolean onTouch(CollisionInfo info);


}
