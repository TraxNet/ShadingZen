package org.traxnet.shadingzen.core;

import android.util.Log;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

public abstract class Collider extends Actor {
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
        if(null == _extends){
            Log.e("ShadingZen", "ba");
        }
        Vector3 half_pos = _extends.mul(0.5f);
        Vector3 half_neg = half_pos.negate();

        return new BBox(_position.add(half_neg), _position.add(half_pos));
	}



    public float getOuterRadius(){
        return _extends.lengthSqrt();
    }

    public float getInnerRadius(){
        return Math.min(_extends.x, Math.min(_extends.y, _extends.z));
    }

	public abstract void onTouch(Collider other);
}
