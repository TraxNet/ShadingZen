package org.traxnet.shadingzen.core;

public interface IShadowCaster{
	public boolean castsShadow();
	
	/** Draw this entity into a depth map 
	 * @throws Exception */
	public void onDepthMapDraw(RenderService renderer) throws Exception;
}
