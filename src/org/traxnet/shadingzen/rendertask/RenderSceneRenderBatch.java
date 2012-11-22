package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.util.ObjectPool;

import android.content.Context;
import android.opengl.GLES20;


public class RenderSceneRenderBatch extends RenderTaskBatch {
	public static ObjectPool _staticPool = null;
	
	public static synchronized  RenderSceneRenderBatch createFromPool() throws InstantiationException, IllegalAccessException{
		if(null == _staticPool)
			_staticPool = new ObjectPool(10);
		
		return (RenderSceneRenderBatch) _staticPool.newObject(RenderSceneRenderBatch.class);

	}

	@Override
	public void onDraw(RenderService service) throws Exception {
		GLES20.glCullFace(GLES20.GL_BACK);
		
		traverseCmdBufferAndExecute(service);
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return true;
	}

	@Override
	public void initializeFromPool() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finalizeFromPool() {
		// TODO Auto-generated method stub
		
	}

}
