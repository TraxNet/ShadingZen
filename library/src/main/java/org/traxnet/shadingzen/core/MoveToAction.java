package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Vector3;

public class MoveToAction extends Action {

	float _secs;
	float _currentSecs;
	boolean _isDone;
	Vector3 _toPosition, _fromPosisition;
	
	public MoveToAction(Vector3 to_pos, float secs){
		_secs = secs;
		_currentSecs = 0.f;
		_isDone = false;
		_toPosition = new Vector3(to_pos);
		_fromPosisition = new Vector3();
	}
	
	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(_isDone)
			return;
		
		_currentSecs += deltaTime;
		
		if(_currentSecs >= _secs){
			_isDone = true;
			_targetActor.setPosition(_toPosition);
			return;
		}

		float step_amount = (1.f - (_secs - _currentSecs)/_secs);
		float x = _fromPosisition.getX() + (_toPosition.getX() - _fromPosisition.getX())*step_amount;
		float y = _fromPosisition.getY() + (_toPosition.getY() - _fromPosisition.getY())*step_amount;
		float z = _fromPosisition.getZ() + (_toPosition.getZ() - _fromPosisition.getZ())*step_amount;
		
		_isDone &= areFloatEpsiloEq(x, _toPosition.getX(), 0.01f);
		_isDone &= areFloatEpsiloEq(y, _toPosition.getY(), 0.01f);
		_isDone &= areFloatEpsiloEq(z, _toPosition.getZ(), 0.01f);
		
		if(_isDone)
			_targetActor.setPosition(_toPosition);
		else
			_targetActor.setPosition(new Vector3(x, y, z));
	}
	
	private boolean areFloatEpsiloEq(float a, float b, float epsilon){
		return Math.abs(a - b) < epsilon;
	}

	@Override
	protected void onRegisterTarget() {
		_fromPosisition.set(_targetActor.getPosition());

	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

}
