package org.traxnet.shadingzen.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class RenderBuffer extends Resource {
	private IntBuffer _bufferIds;
	private ShortBuffer _ib;
    private FloatBuffer _mainb;
    
    float _vertsBuffer[];
    private short _indices[];
    
    int _arrayMode, _elementsMode;
    boolean _needsDriverLoad = true;
    boolean _needBufferUpdates = true;
	
	public void init(int max_array_size, int array_mode, int max_elements_size, int elements_mode){
		
		
		_vertsBuffer = new float[max_array_size];
		_mainb = FloatBuffer.wrap(_vertsBuffer);
		
		_indices = new short[max_elements_size];
		_ib = ShortBuffer.wrap(_indices);
		
		_arrayMode = array_mode;
		_elementsMode = elements_mode;
		_needsDriverLoad = true;
		
	}
	
	/*
	 * Copies data from _mainb to our allocated ARRAY_BUFFER. This buffer holds verts data
	 * 
	 *  MUST BE CALLED FROM RENDER THREAD
	 */
	public void updateArrayBuffer(){
		if(null == _mainb || null == _bufferIds)
			return;
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, _mainb.capacity()*4, _mainb);
		GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, 0 );
		
		checkGL("updateArrayBuffer");
	}
	
	/* MUST BE CALLED FROM RENDER THREAD */
	public void updateElementBuffer(){
		if(null == _ib || null == _bufferIds)
			return;
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glBufferSubData(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0, _ib.capacity()*2, _ib);
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
		
		checkGL("updateElementBuffer");
	}
	
	public void bindArrayBuffer(){
		if(null == _ib || null == _bufferIds)
			return;
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		
		checkGL("bindArrayBuffer");
	}
	
	public void bindElementBuffer(){
		if(null == _ib || null == _bufferIds)
			return;
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		
		checkGL("bindElementBuffer");
	}
	
	public float [] getArrayBuffer(){
		return _mainb.array();
	}
	
	public short [] getElementBuffer(){
		return _ib.array();
	}
	
	
	
	void checkGL(String section){
		int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", "VertsListRenderer(" + section + "): glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
        }
	}

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id,
			Object params) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onDriverLoad(Context context) {
		if(_needsDriverLoad){
			Log.v("ShadingZen", "Render buffer " + this.getId() + " loading data into driver...");
			// Allocate buffers at driver space once. But we need to update this data on each frame
			_bufferIds = ByteBuffer.allocateDirect(2 * Integer.SIZE/8).asIntBuffer();
			GLES20.glGenBuffers(2, _bufferIds);
	
			// Generate a buffer in driver space on which we will copy data each frame
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _mainb.capacity()*4, null, _arrayMode /*GLES20.GL_DYNAMIC_DRAW*/);
			GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, 0 );
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, _ib.capacity()*2, null, _elementsMode /*GLES20.GL_STATIC_DRAW*/);
			GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
			
			checkGL("onDriverLoad");
			
			_needsDriverLoad = false;
		}
		
		
		if(_needBufferUpdates){
			updateArrayBuffer();
			updateElementBuffer();
			_needBufferUpdates =false;
		}
		
		return true;
	}

	@Override
	public boolean onResumed(Context context) {
		_needsDriverLoad = true;
		_needBufferUpdates = true;
		return true;
	}

	@Override
	public boolean onPaused(Context context) {
		_needsDriverLoad = true;
		_needBufferUpdates = true;
		return true;
	}

	@Override
	public void onRelease() {
		GLES20.glDeleteBuffers(2, _bufferIds);
		_needsDriverLoad = true;
	}

	@Override
	public boolean isDriverDataDirty() {
		// we need to update driver data content on each frame
		return _needsDriverLoad || _needBufferUpdates;
	}
}
