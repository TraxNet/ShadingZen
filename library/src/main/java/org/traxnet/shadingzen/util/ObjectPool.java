package org.traxnet.shadingzen.util;

public class ObjectPool {
	protected final int MAX_FREE_OBJECT_INDEX;

	protected Poolable[] freeObjects;
	protected int freeObjectIndex = -1;

	/**
	 * Constructor.
	 *
	 * @param factory the object pool factory instance
	 * @param maxSize the maximun number of instances stored in the pool
	 */
	public ObjectPool(int maxSize)
	{
		this.freeObjects = new Poolable[maxSize];
		MAX_FREE_OBJECT_INDEX = maxSize - 1;
	}
	

	/**
	 * Creates a new object or returns a free object from the pool.
	 *
	 * @return a PoolObject instance already initialized
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public synchronized Poolable newObject(Class<? extends Poolable> _class) throws InstantiationException, IllegalAccessException
	{
		Poolable obj = null;

		if (freeObjectIndex == -1)
		{
			// There are no free objects so I just
			// create a new object that is not in the pool.
			obj = (Poolable) _class.newInstance();
		}
		else
		{
			// Get an object from the pool
			obj = freeObjects[freeObjectIndex];

			freeObjectIndex--;
		}

		// Initialize the object
		obj.initializeFromPool();

		return obj;
	}

	/**
	 * Stores an object instance in the pool to make it available for a subsequent
	 * call to newObject() (the object is considered free).
	 *
	 * @param obj the object to store in the pool and that will be finalized
	 */
	public synchronized void freeObject(Poolable obj)
	{
		if (obj != null)
		{
			// Finalize the object
			obj.finalizeFromPool();

			// I can put an object in the pool only if there is still room for it
			if (freeObjectIndex < MAX_FREE_OBJECT_INDEX)
			{
				freeObjectIndex++;

				// Put the object in the pool
				freeObjects[freeObjectIndex] = obj;
			}
		}
	}
}
