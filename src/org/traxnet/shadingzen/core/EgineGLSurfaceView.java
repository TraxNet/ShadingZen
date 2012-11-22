package org.traxnet.shadingzen.core;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import org.traxnet.shadingzen.core.font.BMFont;

public class EgineGLSurfaceView extends GLSurfaceView implements RenderNotificationsDelegate {
	private org.traxnet.shadingzen.core.Renderer _openglRenderer;
	private Context _context;
	private Handler _handler;
	private Engine _engine;
	
	public EgineGLSurfaceView(Context context, org.traxnet.shadingzen.core.Renderer renderer){
		super(context);	
		_context = context;
		_openglRenderer = renderer;
		setEGLContextClientVersion(2); // Pick an OpenGL ES 2.0 context.
		//setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
		_openglRenderer.setDelegate(this);
		this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		//setEGLConfigChooser(new MultisampleConfigChooser());
		setRenderer(_openglRenderer);
		setRenderMode(RENDERMODE_CONTINUOUSLY);
		
		
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		int width = display.getWidth();
		int height = display.getHeight();
		
		_engine = new Engine(width, height);
		_engine.Init(_context, _openglRenderer);
		
		
		
	}
	
	public Engine getEngine(){
		return _engine;
	}

	@Override
	public void surfaceCreated (SurfaceHolder holder){
		super.surfaceCreated(holder);
		
		
	}
	
	@Override
	public void onPause() {
		ResourcesManager.getSharedInstance().onPaused();
		
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
    }
	    
    @Override
    public void onResume() {
    	_openglRenderer.forceReloadResouces();
    	ResourcesManager.getSharedInstance().onResumed();
		
    	
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        
    }

	@Override  
    public boolean onTouchEvent(MotionEvent event)  
    {  
        //This avoids touchscreen events flooding the main thread  
        synchronized (event)  
        {  
        	//Waits 16ms.  
            try {
				event.wait(16);
				
				return _engine.onTouchEvent(event);
			} catch (InterruptedException e) {
				  return true;  
			}    
        	
        }
    }
	
	@Override
	public void onRenderCreated() {
		Log.i("ShadingZen", "Renderer has been initialized");
	}
}
