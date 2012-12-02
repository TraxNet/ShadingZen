package org.traxnet.shadingzen.core;

public interface InputController{
	public abstract void onTouchDrag(float posx, float posy, float deltax, float deltay);
	public abstract void onTouchUp(float posx, float posy);
	public abstract boolean onScaleGesture(float scale_factor);
}
