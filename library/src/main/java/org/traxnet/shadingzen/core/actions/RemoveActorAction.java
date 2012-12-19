package org.traxnet.shadingzen.core.actions;

import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.InvalidTargetActorException;

public class RemoveActorAction extends Action {
	float _secs;
	float _currentSecs;
	boolean _isDone;
	
	public RemoveActorAction(float secs){
		_secs = secs;
		_currentSecs = 0.f;
		_isDone = false;
	}
	
	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(_isDone)
			return;
		
		_currentSecs += deltaTime;
		
		if(_currentSecs >= _secs){
			_targetActor.markForDestroy();
			_targetActor.setScale(0.f);
			_isDone = true;
		}

	}

	@Override
	protected void onRegisterTarget() {
		

	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

}
