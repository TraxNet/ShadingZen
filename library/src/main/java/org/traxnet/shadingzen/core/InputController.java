package org.traxnet.shadingzen.core;

public interface InputController{
	public abstract boolean onTouchDrag(int pointer_id, float posx, float posy, float deltax, float deltay);
	public abstract boolean onTouchUp(int pointer_id, float posx, float posy);
    public abstract boolean onTouchDown(int pointer_id, float posx, float posy);
	public abstract boolean onScaleGesture(float scale_factor);
}
