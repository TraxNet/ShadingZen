package org.traxnet.shadingzen.core;

/** Renderer Notifications interface 
 * 
 * All calsl are done using the OpenGL thread which should
 * help loading content into the driver. 
 * 
 * @author oscar
 *
 */
public interface  RenderNotificationsDelegate {
	/** Called once the renderer is ready for content loading */
	public void onRenderCreated();
	
	
}
