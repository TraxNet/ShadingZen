package org.traxnet.shadingzen.core;

public interface ShadowCasterInterface {
	
	
	/** Draw this entity into a depth map 
	 * @throws Exception */
	public abstract void onDepthMapDraw(RenderService renderer) throws Exception;
}
