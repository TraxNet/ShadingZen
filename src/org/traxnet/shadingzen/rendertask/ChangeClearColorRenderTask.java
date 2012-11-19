package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.Vector4;

import android.content.Context;
import android.opengl.GLES20;

public class ChangeClearColorRenderTask extends RenderTask {
	Vector4 _clearColor;
	
	public ChangeClearColorRenderTask(Vector4 color){
		_clearColor = color;
	}

	@Override
	public void onDraw(RenderService service) {
		GLES20.glClearColor(_clearColor.getX(), _clearColor.getY(), _clearColor.getZ(), _clearColor.getW());
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return true;
	}

}
