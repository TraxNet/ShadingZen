package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.Vector4;

import android.content.Context;
import android.opengl.GLES20;

public class ChangeClearColorRenderTask extends RenderTask {
	Vector4 _clearColor = Vector4.zero();
	
	public ChangeClearColorRenderTask(){}
	
	public void setColor(float r, float g, float b, float a){
		_clearColor.set(r, g, b, a);
	}

	@Override
	public void onDraw(RenderService service) {
		GLES20.glClearColor(_clearColor.getX(), _clearColor.getY(), _clearColor.getZ(), _clearColor.getW());
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return true;
	}

	@Override
	public void initializeFromPool() {
		_clearColor.set(0.f, 0.f, 0.f, 0.f);
	}

	@Override
	public void finalizeFromPool() {
		
	}

}
