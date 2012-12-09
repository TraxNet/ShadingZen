package org.traxnet.shadingzen.util;

public interface PoolObjectFactory {
	/**
	 * Creates a new object for the object pool.
	 *
	 * @return new object instance for the object pool
	 */
	public Poolable createPoolObject();
}
