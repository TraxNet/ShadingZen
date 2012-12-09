/**
 * 
 */
package org.traxnet.shadingzen.core;

/** Buffer objects that can be used as texture lookups during rendering must conform to this interface.
 * 
 * @author Oscar Blasco
 *
 */
public interface Texture {
	/** Binds the texture to the given texture unit. Called from rendering thread 
	 *
	 * Note: Texture must be already loaded into driver memory before this method is called
	 */
	public void bindTexture(int unit);
	/** Unbinds texture. Called from rendering thread */
	public void unbindTexture(int unit);
	
	public int getWidth();
	public int getHeight();
}
