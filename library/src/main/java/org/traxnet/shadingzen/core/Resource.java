package org.traxnet.shadingzen.core;

import android.content.Context;

public abstract class Resource {
	private int _refs;
	protected Object _lock;
	private boolean _isMarkedForRelease;
	protected String _id;
	
	public Resource(){
		_refs = 0;
		_lock = new Object();
		_isMarkedForRelease = false;
	}
	
	public abstract boolean onStorageLoad(Context context, String id, int resource_id, Object params); 
	public abstract boolean onDriverLoad(Context context);
	public abstract boolean onResumed(Context context);
	public abstract boolean onPaused(Context context);
	public abstract void onRelease();
	public abstract boolean isDriverDataDirty();
	
	public void setId(String id){
		_id = id;
	}
	
	public String getId(){
		return _id;
	}
	
	public void addRef(){
		synchronized(_lock){
			_refs++;
			unmarkForRelease();
		}
	}
	public void removeRef(){
		synchronized(_lock){
			_refs--;
			
			if(0 >= _refs)
				markForRelease();
		}
	}
	
	void markForRelease(){
		_isMarkedForRelease = true;
	}
	void unmarkForRelease(){
		_isMarkedForRelease = false;
	}
	public boolean needsRelease(){
		synchronized(_lock){
			return _isMarkedForRelease;
		}
	}
}
