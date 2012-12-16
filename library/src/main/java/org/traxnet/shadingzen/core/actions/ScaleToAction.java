package org.traxnet.shadingzen.core.actions;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.InvalidTargetActorException;

public class ScaleToAction extends Action {
	float _secs = 0.f;
	float _targetSecs = 0.f;
	float _startScale = 0.f;
	float _targetScale = 0.f;
	boolean _isDone = false;
	
	/**
	 * Scale to a new factor in the specified amount of time
	 * @param secs Secs for this transition
	 * @param scale Target scale factor
	 */
	public ScaleToAction(float secs, float scale){
		_targetScale = scale;
		_targetSecs = secs;
	}

	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(isDone()){
			_targetActor.removeAction(this);
			return;
		}
		
		
		if(null == _targetActor || !Actor.class.isInstance(_targetActor))
			throw new InvalidTargetActorException();
		
		if(_secs >= _targetSecs){
			_targetActor.setScale(_targetScale);
			_isDone = true;
			return;
		}
		
		_secs += deltaTime;
		float current_scale = _startScale + (_targetScale - _startScale)*(1.f - (_targetSecs - _secs)/_targetSecs);
		
		_targetActor.setScale(current_scale);
	}

	@Override
	protected void onRegisterTarget() {
		_startScale = _targetActor.getScale();
	}

	@Override
	public boolean isDone() {	
		return _isDone;
	}

}
