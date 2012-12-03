package org.traxnet.shadingzen.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;

public class TextureRenderTarget extends Resource implements Texture {
	protected int _target;
	protected IntBuffer _textureIds;
	protected int[] _frameBufferObjects = new int[1];
	protected int[] _renderBufferObjects = new int[1];
	protected boolean _driverDataDirty = true;
	protected int _width, _height;
	
	public void init(int width, int height){
		_width = width;
		_height = height;
	}
	
	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id,
			Object params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDriverLoad(Context context) {
		_textureIds = IntBuffer.allocate(1);
		GLES20.glGenFramebuffers(1, _frameBufferObjects, 0);
		GLES20.glGenRenderbuffers(1, _renderBufferObjects, 0); // the depth buffer
		GLES20.glGenTextures(1, _textureIds);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureIds.get(0)); 
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

		IntBuffer  texBuffer = ByteBuffer.allocateDirect(_width * _height
				* 2).order(ByteOrder.nativeOrder()).asIntBuffer();;

		// generate the textures
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, _width, _height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);

		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, _renderBufferObjects[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, _width, _height);
				
		_driverDataDirty = false;
		
		return true;
	}

	@Override
	public boolean onResumed(Context context) {
		_driverDataDirty = true;
		return true;
	}

	@Override
	public boolean onPaused(Context context) {
		_driverDataDirty = true;
		return true;
	}

	@Override
	public boolean isDriverDataDirty() {
		return _driverDataDirty;
	}

	@Override
	public void onRelease() {
		GLES20.glDeleteTextures(1, _textureIds);
		GLES20.glDeleteFramebuffers(1, _frameBufferObjects, 0);
		GLES20.glDeleteRenderbuffers(1, _renderBufferObjects, 0);

		_textureIds.clear();
	    _textureIds = null;   
	}

	
	public void enableRenderTarget(){
		// viewport should match texture size
		GLES20.glViewport(0, 0, _width, _height);

		// Bind the framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, _frameBufferObjects[0]);

		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, _textureIds.get(0), 0);

		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, _renderBufferObjects[0]);

		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
			return;

		// Clear the texture (buffer) and then render as usual...
		GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
		GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	
	public int getTextureId(){
		return _textureIds.get(0);
	}
	
	public void bindTexture(int unit){	
		if(null == _textureIds){
			_driverDataDirty = true;
			return;
		}
			
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
		GLES20.glBindTexture(_target, _textureIds.get(0));
		
		//checkGlError("bind texture");
	}
	
	public void unbindTexture(int unit){	
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
		GLES20.glBindTexture(_target, 0);
		
	}

	@Override
	public int getWidth() {
		return _width;
	}

	@Override
	public int getHeight() {
		return _height;
	}

}
