package org.traxnet.shadingzen.core;

public interface InputController{
	public abstract boolean onTouchDrag(float posx, float posy, float deltax, float deltay);
	public abstract boolean onTouchUp(float posx, float posy);
	public abstract boolean onScaleGesture(float scale_factor);
}
