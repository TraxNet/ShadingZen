package org.traxnet.shadingzen.core;

/** 
 * @deprecated As of current state this class is deprecated and it will not work
 * @author Oscar
 *
 */
public abstract class SceneTransition extends Scene {
	protected Scene _oldScene, _newScene;
	protected float _targetTime;
	protected float _currentTime;
	private boolean _isReplace = false;
	
	/** If overriden, must be called from child classes */
	public void initWithScene(float time, Scene new_scene){
		_newScene = new_scene;
		_targetTime = time;
		
		_oldScene = Engine.getSharedInstance().getCurrentScene();
	}
	
	protected boolean isDone() {
		return (_currentTime >= _targetTime);
	}
	
	/** If set as true, the old scene is destroyed */
	public void setIsReplace(boolean value){
		_isReplace = value;
	}
	
	protected abstract void onTransitionStep(float delta);
	
	/** If returns false, drawing is done in this order:
	 *  1. Draw old scene
	 *  2. Draw new scene
	 * 	3. Render transition scene hierarchy
	 * @param renderer
	 * @return True if render is handled inside
	 */
	protected abstract boolean onTransitionStepDraw(RenderService renderer);
	
	/** Replaces the SceneTransition scene with the final scene. */
	private void onFinishTranstion(){
		Engine.getSharedInstance().popScene();
		Engine.getSharedInstance().pushScene(_newScene);
		
		if(_isReplace){
			_oldScene.markForDestroy();
			_oldScene.onDestroy();
		}
	}
	
	
	@Override
	public void onTick(float delta) {
		super.onTick(delta);
		_entityManager.updateTick(delta, getLocalModelMatrix().getAsArray());
		
		_currentTime += delta;
		
		_newScene.onTick(delta);
		if(!_oldScene.isPendingDestroy())
			_oldScene.onTick(delta);
		
		onTransitionStep(delta);
		
		if(isDone())
			onFinishTranstion();
	}

	@Override
	public void onDraw(RenderService renderer) {
		
		/** Let derived classes handle drawing themselves. If return false we handle it*/
		if(!onTransitionStepDraw(renderer)){
			// _entityManager.drawHierarchy(renderer); // BROKEN CODE HERE ///////////////////////////////
			
			_newScene.onDraw(renderer);
			if(!_oldScene.isPendingDestroy())
				_oldScene.onDraw(renderer);
		}
		
	}
}
