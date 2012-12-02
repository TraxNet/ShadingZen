package org.traxnet.shadingzen.core;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/** This class is used as synchronization fence for worker threads
 * All threads will wait at this fence before the synchronization step
 * @author oscar
 *
 */
public class BarrierTask extends Task {
	CyclicBarrier _barrier = null;
	
	public BarrierTask(CyclicBarrier barrier){
		_barrier = barrier;
	}

	@Override
	public void run(long deltatime) {
		// TODO Auto-generated method stub
		try {
			_barrier.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void executeSync() {
		

	}

}
