package org.traxnet.shadingzen.core;

import android.os.Handler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class TaskManager {
	static private TaskManager _global_taskManager = null;
	
	private List<Handler> _handlers = null;
	private Queue<Task> _currentTasks = null;
	private CyclicBarrier _barrier = null;
	
	public TaskManager(){
		_handlers = new ArrayList<Handler>();
		_currentTasks = new LinkedList<Task>();
	}
	
	static public void setSharedInstance(TaskManager manager) throws IllegalAccessException{
		//if(null != _global_taskManager) throw new IllegalAccessException();
		
		_global_taskManager = manager;
	}
	
	static public TaskManager getSharedInstance(){
		return _global_taskManager;
	}
	
	/** Adds a new thread to the list
	 * WARNING: Cannot be called once the simulation starts as it reinitializes
	 * shared data among threads and would cause unexpected results
	 * 
	 * @param handler Looper's handler to add
	 */
	public synchronized void registerHandler(Handler handler){
		if(!_handlers.contains(handler))
			_handlers.add(handler);
		
		_barrier = new CyclicBarrier(_handlers.size());
	}
	
	public synchronized void pushTask(Task task){
		_currentTasks.add(task);
	}
	
	public synchronized void distributeTasks(final long deltatime){
		int num_task_per_thread = _currentTasks.size()/_handlers.size();
		int remaining_task_handlers = _handlers.size()-1;
		for(Handler handler : _handlers){
			if(0 == remaining_task_handlers)
				num_task_per_thread = _currentTasks.size();
			
			for(int count = 0; count < num_task_per_thread; count++){
				final Task task = _currentTasks.poll();
				if(null == task)
					break;
				
				handler.post(new Runnable(){
					public void run(){
						task.run(deltatime);
					}
				});
			}
			
			// Add a barrier task at the end of the queue which will
			// synchronized that thread with the main simulation loop
			handler.post(new Runnable(){
				public void run(){
					(new BarrierTask(_barrier)).run(deltatime);
				}
			});
		}
	}

    public synchronized void debugExecuteSync(final long deltatime){
        while(_currentTasks.size() > 0){
            Task task = _currentTasks.poll();
            task.run(deltatime);
        }
    }
	
	public boolean synchronizeTasks(){
		try {
			_barrier.await();
			
			for(Task task : _currentTasks)
				task.executeSync();
			
			return true;
		} catch (InterruptedException e) {
			return false;
		} catch (BrokenBarrierException e) {
			return false;
		}
	}
}
