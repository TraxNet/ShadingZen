package org.traxnet.shadingzen.core.actions;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.InvalidTargetActorException;

public abstract class Action {
	protected Actor _targetActor;
	protected boolean _cancelled = false;
	
	/*
	 * Executes one step of this action on target's actor. deltaTime is in seconds
	 * 
	 * Must be overriden by child classes. 'step' is called on every engine's tick before
	 * actor's update method
	 */
	public abstract void step(float deltaTime) throws InvalidTargetActorException;
	
	protected abstract void onRegisterTarget();
	
	public abstract boolean isDone();
	
	public void setTarget(Actor actor){
		_targetActor = actor;
		onRegisterTarget();
	}
	
	public Actor getTarget(){
		return _targetActor;
	}
	
	public void cancel(){
		_cancelled = true;
	}
	
	
}
