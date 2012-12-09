package org.traxnet.shadingzen.core;

/** This interface decouples rendering commands from the target we are rendering to
 * 
 * @author Oscar Blasco
 *
 */
public interface RenderTarget {
	/**
	 * Activates this render target. Must setup an OpenGL render target, for example 
	 * the framebuffer or a texture. Called by the RenderService bef
	 * @param renderer
	 */
	public void bind(RenderService renderer);
	public void unbind(RenderService renderer);
}
