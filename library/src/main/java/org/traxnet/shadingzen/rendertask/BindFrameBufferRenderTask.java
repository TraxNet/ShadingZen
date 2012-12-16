/**
 * 
 */
package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;

import android.content.Context;
import android.opengl.GLES20;

/** Binds the framebuffer as the render target. This is the normal setup for the engine.
 * 
 * @author Oscar Blasco
 *
 */
public class BindFrameBufferRenderTask extends RenderTask {
	protected int _frameBufferId = 0;
	
	public void setFrameBufferId(int id){
		_frameBufferId = id;
	}
	
	public BindFrameBufferRenderTask(){}

	@Override
	public void onDraw(RenderService service) throws Exception {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, _frameBufferId);

		
		GLES20.glDepthMask(true); 
		if(null != service.getBackgroundRenderTask()){
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT); // we may be able to remove this
			service.getBackgroundRenderTask().onDraw(service);
		} else{
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
		}
		
		// Enable back-face culling
        GLES20.glEnable( GLES20.GL_CULL_FACE );

        GLES20.glCullFace(GLES20.GL_BACK);
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return true;
	}

	@Override
	public void initializeFromPool() {
		_frameBufferId = 0;
	}

	@Override
	public void finalizeFromPool() {
		
	}

}
