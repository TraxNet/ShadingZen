package org.traxnet.shadingzen.core2d;

import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.InvalidTargetActorException;

public class FadeInAction extends Action {
	float _secs = 0.f;
	float _targetSecs = 0.f;
	float _startAlpha = 0.f;
	float _targetAlpha = 0.f;
	
	public FadeInAction(float secs, float target_alpha){
		_targetSecs = secs;
		_targetAlpha = target_alpha;
	}

	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(isDone()){
			_targetActor.removeAction(this);
			return;
		}
		
		if(null == _targetActor || !Node2d.class.isInstance(_targetActor))
			throw new InvalidTargetActorException();
		
		_secs += deltaTime;
		float current_alpha = Math.max(0.f, _startAlpha*(_targetSecs - _secs));
		
		Node2d target = (Node2d)_targetActor;
		target.setNodeAlpha(current_alpha);
		
	}
	
	@Override
	public boolean isDone(){
		return _secs >= _targetSecs;
	}
	
	@Override
	protected void onRegisterTarget(){
		Node2d target = (Node2d)_targetActor;
		
		_startAlpha = target.getNodeAlpha();
	}

}
