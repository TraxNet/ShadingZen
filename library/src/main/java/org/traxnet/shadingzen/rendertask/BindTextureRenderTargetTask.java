package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.TextureRenderTarget;

import android.content.Context;

public class BindTextureRenderTargetTask extends RenderTask {
	protected TextureRenderTarget _target;
	
	public BindTextureRenderTargetTask(){}
	
	public void init(TextureRenderTarget target){
		_target = target;
	}
	
	@Override
	public void onDraw(RenderService service) throws Exception {
		_target.enableRenderTarget();
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return _target.onDriverLoad(context);
	}

	@Override
	public void initializeFromPool() {
		
	}

	@Override
	public void finalizeFromPool() {
		_target = null;
	}
	
	
}
