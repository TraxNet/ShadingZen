package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

public abstract class Collider extends Actor {
	//protected BBox _collisionBBox;
	protected Vector3 _extends;
	
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
	
	public BBox getBoundingBox(){
		Vector3 center = this._worldMatrix.mul(new Vector3());
		return new BBox(center, _extends);
	}

	public abstract void onTouch(Collider other);
}
