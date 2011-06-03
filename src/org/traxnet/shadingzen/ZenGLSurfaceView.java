package org.traxnet.shadingzen;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import android.view.SurfaceHolder;
import android.view.KeyEvent;


public class ZenGLSurfaceView extends GLSurfaceView {
	private Renderer _openglRenderer;
	
	public ZenGLSurfaceView(Context context){
		super(context);	
	}
	
	@Override
	public void setRenderer(GLSurfaceView.Renderer renderer){
		_openglRenderer = renderer;
		super.setRenderer(_openglRenderer);
		setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
		this.setRenderMode(RENDERMODE_CONTINUOUSLY);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_DPAD_CENTER == keyCode) {
            queueEvent(new Runnable() {
                // This method will be called on the rendering
                // thread:
                public void run() {
                	// Do something 
                }});
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
