package org.traxnet.shadingzen.rendertask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.util.Poolable;

import android.content.Context;
import android.util.Log;

public abstract class RenderTaskBatch implements Poolable {
	LinkedList<RenderTask> _renderTaskCmdBuffer;
	
	RenderTaskBatch(){
		_renderTaskCmdBuffer = new LinkedList<RenderTask>();
	}
	
	public void AddTask(RenderTask task){
		_renderTaskCmdBuffer.add(task);
	}
	
	protected void traverseCmdBufferAndExecute(RenderService service){
		int num_tasks = _renderTaskCmdBuffer.size();
		for(int i = 0; i < num_tasks; i++){
			
			RenderTask task = _renderTaskCmdBuffer.get(i);
			
			if(!task.onDriverLoad(service.getContext()))
				continue;
						
			// Let the shape draw itself
			try {
				task.onDraw(service);
				service.checkGlError("Task Draw");
				RenderTaskPool.sharedInstance().freeTask(task);
			} catch (Exception e) {
				Log.e("ShadingZen", "Error rendering task of type [" + task.getClass().getName() + "]:" + e.getLocalizedMessage());
				StringWriter sw = new StringWriter();
			    PrintWriter pw = new PrintWriter(sw);
			    e.printStackTrace(pw);
				Log.e("ShadingZen", sw.toString());
			}
			
		}
	}

	
	public abstract void onDraw(RenderService service) throws Exception;	
	public abstract boolean onDriverLoad(Context context);

	public abstract void initializeFromPool();
	public abstract void finalizeFromPool();
	
}
