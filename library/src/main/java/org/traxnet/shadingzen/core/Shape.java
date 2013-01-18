package org.traxnet.shadingzen.core;

/**
 * Shape is a reusable 3d object which may be shared by different Actors. This minimizes memory usage (main and OpenGL memory).
 * As it extends from Resource it must obey load/unload to OpenGL driver memory rules. Please @see org.traxnet.shadingzen.OBJMesh for
 * a more direct example of a Shape subclass.
 */
public interface Shape {


	public abstract void onDraw(RenderService service);
	

}
