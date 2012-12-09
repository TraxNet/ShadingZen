package org.traxnet.shadingzen.core;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Configuration;

public abstract class GameInfo extends Actor {
	protected HashMap<String, Collider> _colliders;
	protected InputController _inputController;
	protected Level _currentLevel;
	protected Camera _currentCamera;
	
	public GameInfo(){
		_colliders = new HashMap<String, Collider>();
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
	
	public void onPreDraw(RenderService renderer){}
	public void onPostDraw(RenderService renderer){}
	
	public abstract void onGameStart();
	public abstract void onGameEnd(); 
	
	/** Called by the engine when the device changes any of its configuration parameters (i.e. orientation */
	public void onConfigurationChanged(Configuration prevconfig, Configuration newconfig){}
	
	/** Called when the user pressed the back "phisical" button on the device
	 * Override as required.
	 * @return True if the event is handled in your code. False to trigger default behaviour (return to previous app activity)
	 */
	public boolean onBackButtonPressed(){
		return false;
	}
}
