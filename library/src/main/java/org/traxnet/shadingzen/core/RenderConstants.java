package org.traxnet.shadingzen.core;

import java.nio.IntBuffer;

import android.opengl.GLES20;

public final class RenderConstants {
	static boolean _initialized = false;
	static int _maxVertexAttribs = -1;
	
	
	public static void initRenderConstants(){
		IntBuffer buff = IntBuffer.allocate(10);
		
		// GL_MAX_VERTEX_ATTRIBS /////
		GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, buff);
		_maxVertexAttribs = buff.get(0);
		
		
		_initialized = true;
	}
	
	
	public static int getMaxVertexAttribs(){
		return _maxVertexAttribs;
	}
	
	public class RenderConstantsNotInitializedException extends Exception{
		private static final long serialVersionUID = 709571970222099386L;
		
	}
}
