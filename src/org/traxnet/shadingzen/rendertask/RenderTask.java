package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.util.Poolable;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

/** A render task sent to the rendering thread 
 * 
 *  It contains a frame stamp, which helps removing old
 *  tasks that doesn't need to be rendered. The rendering
 *  thread will only render task that are from the current frame
 *  or newer (sure?)
 */
public abstract class RenderTask implements Poolable {
	protected long _timeStamp;
	protected Vector4 _diffuseColor;
	protected Vector4 _ambientColor;
	protected int _blendSrc, _blendDst;
	protected boolean _blend = false, _depthTest = false;
	protected ShadersProgram _program;
	protected int _renderingFlags = RenderingFlags.NONE;
	
	public class RenderingFlags {
		public static final int NONE = 0x0000;
		/** This render task contributes to any depth map */
		public static final int CASTS_SHADOW = 0x0001;
		public static final int IS_TRANSPARENT = 0x0002;
	}
	
	public RenderTask(){
		_diffuseColor = new Vector4(1.f, 1.f, 1.f, 1.f);
		_ambientColor = new Vector4(1.f, 1.f, 1.f, 1.f);
	}
	
	public int getRenderingFlags(){
		return _renderingFlags;
	}
	
	public void setFrameStamp(long timestamp)
	{
		_timeStamp = timestamp;
	}
	public long getFrameStamp()
	{
		return _timeStamp;
	}
	
	public void setProgram(ShadersProgram program){ _program = program; }
	
	public ShadersProgram getProgram(){ return _program; } 
	
	public void enableDepthTest(){
		_depthTest = true;
	}
	
	public void disableDepthTest(){
		_depthTest = false;
	}
	
	public void enableBlending(){
		_blend = true;
	}
	
	public void disableBlending(){
		_blend = false;
	}
	
	public void setBlendingFunc(int blend_src, int blend_dst){
		_blendSrc = blend_src;
		_blendDst = blend_dst;
	}
	
	protected void bindBlendingIfSet(){
		if(_blend){
			GLES20.glBlendFunc(_blendSrc, _blendDst);
			GLES20.glEnable(GLES20.GL_BLEND);
		} else{
			GLES20.glDisable(GLES20.GL_BLEND);
		}
	}
	
	protected void bindDepthTestIfSet(){
		if(_depthTest){
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			GLES20.glDepthFunc(GLES20.GL_LEQUAL);
			GLES20.glDepthMask(true);
		} else{
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
	}
	
	protected void unbindBlendingIfSet(){
		/*if(_blend){
			GLES20.glBlendFunc(_blendSrc, _blendDst);
			GLES20.glEnable(GLES20.GL_BLEND);
		} else{
			GLES20.glDisable(GLES20.GL_BLEND);
		}*/
	}
	
	protected void unbindDepthTestIfSet(){
		
	}
	
	public abstract void onDraw(RenderService service) throws Exception;
	
	public abstract boolean onDriverLoad(Context context);
	
	protected void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", op + ": glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
        }
	 }
	
	
	public void setDiffuseColor(float r, float g, float b, float a){
		_diffuseColor.set(r, g, b, a);
	}
	
	
	public void setAmbientColor(float r, float g, float b, float a){
		_ambientColor.set(r, g, b, a);
	}
}
