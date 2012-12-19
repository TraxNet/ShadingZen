package org.traxnet.shadingzen.core.actions;

import org.traxnet.shadingzen.core.Action;
import org.traxnet.shadingzen.core.InvalidTargetActorException;

import java.util.ArrayDeque;

public class SequenceAction extends Action {
	ArrayDeque<Action> _actions;
	boolean _isDone = false;
	Action _current = null;
	
	public SequenceAction(){
		_actions = new ArrayDeque<Action>();
	}
	
	public SequenceAction(Action a, Action b){
		this();
		
		_actions.add(a);
		_actions.add(b);
	}
	
	public void addAction(Action action){
		_actions.add(action);
	}

	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(isDone())
			return;
		
		if(_current.isDone()){
			_actions.pop();
			_current = _actions.peek();
			
			if(null == _current){
				_isDone = true;
				return;
			}
			
			_current.setTarget(_targetActor);
		}

		_current.step(deltaTime);
	}

	@Override
	protected void onRegisterTarget() {
		_current = _actions.peek();
		_current.setTarget(_targetActor);
	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

}
