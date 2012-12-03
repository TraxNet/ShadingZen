package org.traxnet.shadingzen.core;

public class RunDelegateAction extends Action {
	Object _data;
	boolean _isDone = false;
	RunnableDelegate _delegate = null;
	
	public interface RunnableDelegate{
		void run(Actor target, Object data);
	}
	
	public RunDelegateAction(RunnableDelegate delegate, Object data){
		_data = data;
		_delegate = delegate;
	}

	@Override
	public void step(float deltaTime) throws InvalidTargetActorException {
		if(_isDone)
			return;
		
		_delegate.run(_targetActor, _data);
		
		_isDone = true;

	}

	@Override
	protected void onRegisterTarget() {
		

	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

}
