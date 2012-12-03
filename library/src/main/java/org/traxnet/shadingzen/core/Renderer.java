package org.traxnet.shadingzen.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.rendertask.ChangeClearColorRenderTask;
import org.traxnet.shadingzen.rendertask.RenderTask;
import org.traxnet.shadingzen.rendertask.RenderTaskBatch;
import org.traxnet.shadingzen.rendertask.RenderTaskPool;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Android GLSurfaceView.Renderer implementation.
 * Core class of the 3D Engine. Maintains all basic
 * rendering functionality and responsible on all the
 * painting stages.
 */
public class Renderer implements GLSurfaceView.Renderer, RenderService {
	/// Private properties ///////////////
	Camera _currentCamera = new Camera(); 
	HashMap<String, Shape> _shapes;
	float[] _viewMatrix, _projectionMatrix, _orthoProjectionMatrix;
	Context _context;
	float _yaw = 0;
	float _roll = 0;
	float _width, _height;
	RenderNotificationsDelegate _notifyDelegate;
	Boolean _isContentReady;
	ShadersProgram _lastProgram;
	
	Object _renderTasksLock;
	//Stack<RenderTask> _currentRenderTasks;
	LinkedList<RenderTaskBatch> _renderBatchesBuffer;
	RenderTaskBatch _currentBatch = null;
	long _frameTime = 0;
	Vector4 _clearColor;
	boolean _forceReload = false;
	boolean _abortedByException = false;
	RenderTaskBatch [] _tasksArray;
	RenderTask _backgroundRenderTask = null;
	
	
	/// Public methods ///////////////////
	
	public Renderer(Context context){
		_shapes = new HashMap<String, Shape>();
		//_currentRenderTasks = new Stack<RenderTask>();
		_renderBatchesBuffer = new LinkedList<RenderTaskBatch>();
		_context = context;
		_renderTasksLock = new Object();
		_isContentReady = false;
		_lastProgram = null;
		_clearColor = new Vector4(0.f, 0.f, 0.f, 0.f);
		
		_orthoVertsMaxBuffSize = 400*6; // 400 verts (coords + UV)
		_orthoVertsBuffer = new float[_orthoVertsMaxBuffSize];
		_tasksArray = new RenderTaskBatch[1];
	}
	
	
	public void setClearColor(Vector4 color){
		_clearColor = color;
		ChangeClearColorRenderTask task =  (ChangeClearColorRenderTask) RenderTaskPool.sharedInstance().newTask(ChangeClearColorRenderTask.class);
		task.setColor(color.getX(), color.getY(), color.getZ(), color.getW());
		addRenderTask(task);
		
	}
	 
	
	/**
	 *  Draws the entire visible objects.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		if(false == _isContentReady)
			return;
		if(this._abortedByException)
			return;
		
		// Reset orthogonal vertices buffer for drawing HUD 
		_orthoVertsCurrentIndex = 0;
		
		if(ResourcesManager.getSharedInstance().IsDataPaused())
			return;
		
		try{
			int num_batches = 0;
			long delta_time = System.nanoTime() - _frameTime;
			
			TaskManager.getSharedInstance().synchronizeTasks();
			
			synchronized(_renderTasksLock){
				_tasksArray = _renderBatchesBuffer.toArray(_tasksArray);
				num_batches = _renderBatchesBuffer.size();
				_renderBatchesBuffer.clear();
			}
			
			TaskManager.getSharedInstance().distributeTasks(delta_time);
			
			
			if(_forceReload){
				GLES20.glClearColor(_clearColor.getX(), _clearColor.getY(), _clearColor.getZ(), _clearColor.getW());
				
				
				_forceReload = false;
			}
			ResourcesManager.getSharedInstance().loadAllToRenderer();
			
			
			_yaw += 0.5*3.14/360;
			
			this.computeMVPMatricesForCurrentCamera();
	
			/*
			// Clean our drawing buffers
			
			// On some mobiles, glDepthMask(true) is needed for the depth buffer to be cleared correctly
			GLES20.glDepthMask(true); 
			if(null != _backgroundRenderTask){
				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT); // we may be able to remove this
				_backgroundRenderTask.onDraw(this);
			} else{
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
			}
			
			// Enable back-face culling and depth testing
			// TODO: Ensure we are not changing this during the frame and move
			// all this call to the setup-engine step
			//GLES20.glCullFace(GLES20.GL_BACK);
			*/
			
			for(int i = 0; i < num_batches; i++){
				
				RenderTaskBatch batch = _tasksArray[i];
				
				if(!batch.onDriverLoad(_context))
					continue;
							
				batch.onDraw(this);
			
			}
			
			ResourcesManager.getSharedInstance().doCleanUp();

	
			// Send all command to hardware 
			// TODO: Check how Android/GLSurfaceView is handling this as
			// we may either need to call glFinish or avoid any sync command at all
			//GLES20.glFinish();
		
			// UPDATE: Android perform a glSwap command after calling to this method
			// which ensure the commands are going to be rendered properly to the
			// back buffer. 
			
			Engine.getSharedInstance().updateTick();
			
			Engine.getSharedInstance().drawFrame(this);
			
			// TODO: Perform data release here 
		} catch(Exception e){
			Log.e("ShadingZen", "Error rendering frame:" + e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
			Log.e("ShadingZen", sw.toString());
			this._abortedByException = true;
		}
	}
	
	public void setDelegate(RenderNotificationsDelegate delegate){
		_notifyDelegate = delegate;
	}
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		_width = width;
		_height = height;
		
		GLES20.glViewport(0, 0, (int)_width, (int)_height);
		GLES20.glClearColor(_clearColor.getX(), _clearColor.getY(), _clearColor.getZ(), _clearColor.getW());

		
		
		if(null != _currentCamera){
			_currentCamera.setAspect(0.707f, _width/_height);
			_currentCamera.setViewportSize(_width, _height);
		}
	}
	
	public void onTouchEvent(float x, float y){
		_yaw -= x*0.1*3.14/360;
		_roll -= y*0.1*3.14/360;
	}
	
	public void forceReloadResouces(){
		_forceReload = true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Force a load to the driver
		//ResourcesManager.getSharedInstance().loadAllToRenderer();
		//_forceReload = false;
		
		RenderConstants.initRenderConstants();
		
		GLES20.glDisable(GLES20.GL_DITHER);
		
		forceReloadResouces();
		
		
		if(null != _notifyDelegate && !_isContentReady){
			_notifyDelegate.onRenderCreated();
			_isContentReady = true;
		}
		
		
	}
	
	
	public Camera getCamera(){
		return _currentCamera;
	}
	
	public void setCamera(Camera camera){
		_currentCamera = camera;
	}
	
	public void computeMVPMatricesForCurrentCamera(){
		_viewMatrix = _currentCamera.getViewMatrix().getAsArray();
		_projectionMatrix = _currentCamera.getProjectionMatrix().getAsArray();
		_orthoProjectionMatrix = _currentCamera.getOrthoProjectionMatrix().getAsArray();
	}
	
	public void pushTask(RenderTask task){
		synchronized(_renderTasksLock){
			_currentBatch.AddTask(task);
		}
	}
	
	public void setBackgroundRenderTask(RenderTask task){
		_backgroundRenderTask = task;
	}
	
	public RenderTask getBackgroundRenderTask(){
		return _backgroundRenderTask;
	}
	
	/// Private methods ///////////////////
	
	public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", op + ": glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
        }
	 }

	/*** Helper method to setup global shader vars
	 * Global data is passed into the shaders using uniforms
	 * @param program Program to be used
	 * @param shape Used to compute its rigid-body transform
	 */
	@SuppressWarnings("unused")
	private void setGlobalUniformVariables(ShadersProgram program, Shape shape) throws Exception{
		//Log.i("ShadingZen", "Setting global uniform variables for this program");
		float [] mvp = new float[16];
		float [] rot = new float[16];
		float [] mv = new float[16];
		float [] rot_inv = new float[16];
		
		// Model matrix
		Matrix.setIdentityM(rot, 0);
		
		Matrix.rotateM(rot, 0, this._yaw*50, 0.f, 1.f, 0.f);
		Matrix.rotateM(rot, 0, this._roll*50, 0.f, 0.f, -1.f);
		
			
		// Calculate model-view-projection matrix
		Matrix.multiplyMM(mv, 0, _viewMatrix, 0, rot, 0);
		GLES20.glUniformMatrix4fv(program.getUniformLocation("mv_matrix"), 1, false, mv, 0);
		Matrix.multiplyMM(mvp, 0, _projectionMatrix, 0, mv, 0);
		// Move to the shaders as an uniform
		GLES20.glUniformMatrix4fv(program.getUniformLocation("mvp_matrix"), 1, false, mvp, 0);
		
		// Calculate normal matrix, which is the inverse (transpose as it is 3x3 rot matrix) of the
		// model matrix
		//Matrix.transposeM(rot_inv, 0, rot, 0);
		Matrix4 rot_matrix = new Matrix4(rot);
		GLES20.glUniformMatrix3fv(program.getUniformLocation("normal_matrix"), 1, false, rot_matrix.getAsArray3x3(), 0);
		
		GLES20.glUniform3fv(program.getUniformLocation("eye_point"), 1, _currentCamera.getPosition().getAsArray(), 0);
		
	}
	
	Matrix4 buildOrto(){
		float [] data = new float[16];
		Matrix.orthoM(data, 0, 0, _width, 0, _height, 0.1f, 10.f);
		return new Matrix4(data);
	}
	
	
	/// RenderService interface implementation /////////////////

	@Override
	public void setProgram(ShadersProgram program) {
		
		//if(null != _lastProgram && _lastProgram.getProgramId() != program.getProgramId()){
			_lastProgram = program;
			Resource as_resource = (Resource)program;
			if(as_resource.isDriverDataDirty())
				as_resource.onDriverLoad(_context);
			
			program.bindProgram();
		//}
		checkGlError("bind program");
	}
	
	@Override
	public float[] getViewMatrix()
	{
		return _viewMatrix;
	}
	
	@Override
	public float[] getProjectionMatrix()
	{
		return _projectionMatrix;
	}
	
	@Override
	public float[] getOrthoProjectionMatrix(){
		return this._orthoProjectionMatrix;
	}
	
	
	@Override
	public void addRenderTask(RenderTask task)
	{
		synchronized(_renderTasksLock){
			if(null == _currentBatch)
			{
				Log.e("ShadingZen", "Renderer.addRenderTask: no current batch available");
				return;
			}
			_currentBatch.AddTask(task);
		}
	}
	
	@Override
	public void pushRenderBatch(RenderTaskBatch batch) {
		synchronized(_renderTasksLock){
			_renderBatchesBuffer.add(batch);
			_currentBatch = batch;
		}
		
	}
	
	@Override
	public Context getContext(){
		return _context;
	}
	
	float[] _orthoVertsBuffer;
	int _orthoVertsMaxBuffSize = 0;
	int _orthoVertsCurrentIndex = 0;
	
	void setBuffer(float[] buff, int size){
		_orthoVertsBuffer = buff;
		_orthoVertsMaxBuffSize = size;
	}
	
	public float[] getOrthoVertsBuffer(){
		return _orthoVertsBuffer;
	}
	
	public int getOrthoVertsMaxBufferSize(){
		return _orthoVertsMaxBuffSize;
	}
	
	public int getCurrentOrthoVertsIndex(){
		return _orthoVertsCurrentIndex;
	}


	
}
