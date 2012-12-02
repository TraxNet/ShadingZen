package org.traxnet.shadingzen.rendertask;

import org.traxnet.shadingzen.core.Model;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.core.Shape;
import org.traxnet.shadingzen.core.BitmapTexture;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Vector4;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class RenderModelTask extends RenderTask {
	Shape _shape;
	Matrix4 _modelMatrix = new Matrix4();
	BitmapTexture _texture;
	float [] _mvp = new float[16];
	float [] _mv = new float[16];
	boolean _isDepthOnly = false;
	
	
	public void init(ShadersProgram program, Shape shape, Matrix4 model_matrix, BitmapTexture texture){
		_program = program;
		_shape = shape;
		_modelMatrix.set(model_matrix);
		_texture = texture;
		
		
		
	}
	
	public RenderModelTask(){
	}
	
	protected void init(Model model, Matrix4 model_matrix){
		_program = model.getShadersProgram();
		_shape = model.getShape();
		_modelMatrix = model_matrix;
		_texture = model.getTexture();
		_depthTest = true;
		_blend = false;
	}
	
	/*
	public static RenderModelTask buildTask(ShadersProgram program, Shape shape, Matrix4 model_matrix, BitmapTexture texture){
		return new RenderModelTask(program, shape, model_matrix, texture);	
	}
	public static RenderModelTask buildTask(Model model, Matrix4 model_matrix){
		RenderModelTask task = new RenderModelTask(model, model_matrix);
		return task;
	}
	/** Creates a render task that only contributes to depth buffer */
	/*public static RenderModelTask buildDepthOnlyTask(Model model, Matrix4 model_matrix){
		RenderModelTask task = new RenderModelTask(model, model_matrix);
		task._isDepthOnly = true;
		return task;
	}*/
	

	@Override
	public void onDraw(RenderService service) throws Exception {
		bindDepthTestIfSet();
		

		service.setProgram(_program);
	
		
		GLES20.glUniform1i(_program.getUniformLocation("tex_unit"), 0);
		setGlobalUniformVariables(service);
		
		if(null != _texture)
			_texture.bindTexture(0);
		
		_shape.onDraw(service);
		
		if(null != _texture)
			_texture.unbindTexture(0);
		
		_program.unbindProgram();
	}
	
	@Override
	public boolean onDriverLoad(Context context){
		// Load all resources
		/*
		boolean has_failed = false;
		if(_program.isDriverDataDirty())
			has_failed |= !_program.onDriverLoad((context));
		if(Resource.class.isInstance(_shape) && ((Resource)_shape).isDriverDataDirty())
			has_failed |=  !((Resource)_shape).onDriverLoad((context));
		if(_texture.isDriverDataDirty())
			_texture.onDriverLoad(context);
		*/
		
		//return !has_failed;
		return true;
	}
	
	
	private void setGlobalUniformVariables(RenderService renderer) throws Exception {	
		// Calculate model-view-projection matrix
		Matrix.multiplyMM(_mv, 0, renderer.getViewMatrix(), 0, _modelMatrix.getAsArray(), 0);
		GLES20.glUniformMatrix4fv(_program.getUniformLocationNoCheck("mv_matrix"), 1, false, _mv, 0);
		
		Matrix.multiplyMM(_mvp, 0, renderer.getProjectionMatrix(), 0, _mv, 0);
		// Move to the shaders as an uniform
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, _mvp, 0);
		
		GLES20.glUniformMatrix3fv(_program.getUniformLocation("normal_matrix"), 1, false, _modelMatrix.getAsArray3x3(), 0);
		
		GLES20.glUniform3fv(_program.getUniformLocationNoCheck("eye_point"), 1, renderer.getCamera().getPosition().getAsArray(), 0);
		
		GLES20.glUniform4fv(_program.getUniformLocationNoCheck("diffuse_color"), 1, _diffuseColor.getAsArray(), 0);
		
		GLES20.glUniform4fv(_program.getUniformLocationNoCheck("ambient_color"), 1, _ambientColor.getAsArray(), 0);
		
		checkGlError("RenderModelTask.setGlobalUniformVariables");
	}

	@Override
	public void initializeFromPool() {
		_diffuseColor.set(1.f, 1.f, 1.f, 1.f);
		_ambientColor.set(1.f, 1.f, 1.f, 1.f);
		_blendDst = 0;
		_blendSrc = 0;
		_depthTest = true;
		_blend = false;
		
		
		_renderingFlags = 0;
	}

	@Override
	public void finalizeFromPool() {
		_program = null;
		
	}

}
