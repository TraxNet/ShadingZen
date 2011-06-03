package org.traxnet.shadingzen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;

import android.content.Context;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
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
public class Renderer implements GLSurfaceView.Renderer {
	/// Private properties ///////////////
	Camera _currentCamera; 
	HashMap<String, Shape> _shapes;
	Matrix4 _viewMatrix, _projectionMatrix;
	
	/// Public methods ///////////////////
	
	public Renderer(Context context){
		_shapes = new HashMap<String, Shape>();
		
	}
	
	/**
	 *  Draws the entire visible objects.
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		// Clean our drawing buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
		
		
		// Enable back-face culling and depth testing
		// TODO: Ensure we are not changing this during the frame and move
		// all this call to the setup-engine step
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_EQUAL);
		GLES20.glDepthMask(true);
		
		_viewMatrix = _currentCamera.getViewMatrix();
		_projectionMatrix = _currentCamera.getProjectionMatrix();
		
		// Render all shapes 
		Collection<Shape> values = _shapes.values();
		Iterator<Shape> iter = values.iterator();
		ShadersProgram _lastProgram = null;
		while(iter.hasNext()){
			
			Shape shape = (Shape) iter.next();
			
			// Load the specified program
			ShadersProgram program = shape.getProgram();

			if(_lastProgram != program){
				_lastProgram = program;
				program.bindProgram();
			}
				

			
			// Load all uniform variables into the program
			setGlobalUniformVariables(_lastProgram, shape);
			
			// Let the shape draw itself
			shape.onDraw(gl);
		}

		// Send all command to hardware 
		// TODO: Check how Android/GLSurfaceView is handling this as
		// we may either need to call glFinish or avoid any sync command at all
		// GLES20.glFlush();
		
		// UPDATE: Android perform a glSwap command after calling to this method
		// which ensure the commands are going to be rendered properly to the
		// back buffer. 
	}
	
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		GLES20.glClearColor(0.f, 0.f, 0.f, 0.f);

		
		// TODO: Check this with the camera 
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
	}
	
	public Camera getCamera(){
		return _currentCamera;
	}
	
	public void setCamera(Camera camera){
		_currentCamera = camera;
	}
	
	/*** Add a shape into the scene
	 * This will be updated in the future to use another type of object to 
	 * store data needed to render the shape. Shapes should be shared by 
	 * different objects in different positions and rotations.
	 */
	public void pushShape(Shape shape){
		Log.i("Renderer.pushShape", "Pushing shape with id=" + shape.getId() + " into the renderer.");
		_shapes.put(shape.getId(), shape);
	}
	/*** Remove a shape from the scene
	 * 
	 * @param id Shape ID to remove
	 */
	public void removeShape(String id){
		Log.i("removeShape", "Removing shape with id=" + id + " from the renderer.");
		_shapes.remove(id);
	}
	
	/// Private methods ///////////////////

	/*** Helper method to setup global shader vars
	 * Global data is passed into the shaders using uniforms
	 * @param program Program to be used
	 * @param shape Used to compute its rigid-body transform
	 */
	private void setGlobalUniformVariables(ShadersProgram program, Shape shape){
		Matrix4 mvpMatrix = _viewMatrix.Mul(_projectionMatrix);
		
		int mvp_location = program.getUniformLocation("mvp_matrix");
		GLES20.glUniformMatrix4fv(mvp_location, 1, false, mvpMatrix.getAsArray(), 0);
	}
}
