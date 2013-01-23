package org.traxnet.shadingzen.core;

import android.content.res.Configuration;

import java.util.HashMap;

public abstract class GameInfo extends Actor {
	protected HashMap<String, Collider> _colliders;
	protected InputController _inputController;
	protected Level _currentLevel;
	protected Camera _currentCamera;
	
	public GameInfo(){
		_colliders = new HashMap<String, Collider>();
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
