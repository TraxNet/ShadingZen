package org.traxnet.shadingzen.util;

/** Interface that must be implemented by any object that wish to be used as poolable object from PoolFactory
 * 
 * @author Oscar
 *
 */
public interface Poolable {
	public void initializeFromPool();
	public void finalizeFromPool();
}
