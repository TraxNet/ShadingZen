package org.traxnet.shadingzen.core;

public class DelayAction extends Action {
	float _secs;
	float _currentSecs;
	boolean _isDone;
	
	public DelayAction(float secs){
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
