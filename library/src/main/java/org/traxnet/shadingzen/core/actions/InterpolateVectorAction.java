package org.traxnet.shadingzen.core.actions;

import org.traxnet.shadingzen.core.InvalidTargetActorException;
import org.traxnet.shadingzen.math.Vector3;

/**
 * Interpolates a vector from an initial value to a final value. 
 * Vector coordinates are linearly interpolated by a common step amount which depends on the animation time
 *
 */
public class InterpolateVectorAction extends Action {
	Vector3 _targetVar, _startValues, _finalValues;
	float _currentSecs, _targetSecs;
	boolean _isDone = false;
	
	/**
	 * Default constructor
	 * @param target Vector3 object to apply to this interpolation
	 * @param final_values Final value to give to the target Vector3 object
	 * @param secs Time to end the interpolation
	 */
	public InterpolateVectorAction(Vector3 target, Vector3 final_values, float secs){
		_targetVar = target;
		_finalValues = final_values;
		_startValues = new Vector3(target);
		_targetSecs = secs;
		_currentSecs = 0.f;
	}

	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(isDone()){
			_targetActor.removeAction(this);
			return;
		}
		
		if(_currentSecs >= _targetSecs){
			_isDone = true;
			return;
		}
		
		_currentSecs += deltaTime;
		float step_amount = (1.f - (_targetSecs - _currentSecs)/_targetSecs);
		float x = _startValues.getX() + (_finalValues.getX() - _startValues.getX())*step_amount;
		float y = _startValues.getY() + (_finalValues.getY() - _startValues.getY())*step_amount;
		float z = _startValues.getZ() + (_finalValues.getZ() - _startValues.getZ())*step_amount;
		
		_isDone &= areFloatEpsiloEq(x, _finalValues.getX(), 0.01f);
		_isDone &= areFloatEpsiloEq(y, _finalValues.getY(), 0.01f);
		_isDone &= areFloatEpsiloEq(z, _finalValues.getZ(), 0.01f);
		
		//Log.v("ShadingZen", "interp z: " + z);

		_targetVar.set(x, y, z);
	}
	
	private boolean areFloatEpsiloEq(float a, float b, float epsilon){
		return Math.abs(a - b) < epsilon;
	}

	@Override
	protected void onRegisterTarget() {
	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

}
