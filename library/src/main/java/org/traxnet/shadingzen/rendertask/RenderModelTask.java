package org.traxnet.shadingzen.rendertask;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.math.Matrix4;

public class RenderModelTask extends RenderTask {
	Shape _shape;
	Matrix4 _modelMatrix = new Matrix4();
	Texture _texture;
	float [] _mvp = new float[16];
	float [] _mv = new float[16];
	boolean _isDepthOnly = false;
	
	
	public void init(ShadersProgram program, Shape shape, Matrix4 model_matrix, Texture texture){
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
		_modelMatrix.set(model_matrix);
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
		
		//_program.unbindProgram();
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
	
	float [] fake_light =  {400.f, 200.f, 0, 0};
    float [] color = {1.f, 1.f, 1.f, 1.f};

	private void setGlobalUniformVariables(RenderService renderer) throws Exception {	
		// Calculate model-view-projection matrix
		Matrix.multiplyMM(_mv, 0, renderer.getViewMatrix(), 0, _modelMatrix.getAsArray(), 0);
		GLES20.glUniformMatrix4fv(_program.getUniformLocationNoCheck("mv_matrix"), 1, false, _mv, 0);
		
		Matrix.multiplyMM(_mvp, 0, renderer.getProjectionMatrix(), 0, _mv, 0);
		// Move to the shaders as an uniform
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, _mvp, 0);

        Matrix.invertM(_mv, 0,  _modelMatrix.getAsArray(), 0);
        Matrix4 normal_matrix = new Matrix4(_mv);
        //Matrix.transposeM(normal_matrix.getAsArray(), 0, _mv, 0);


        //GLES20.glUniformMatrix4fv(_program.getUniformLocation("invm_matrix"), 1, false, normal_matrix.getAsArray(), 0);

       // Vector4 eLight =  renderer.getCamera().getViewMatrix().mul(new Vector4(400.f, 200.f, 0.f, 1.f));

		GLES20.glUniform3fv(_program.getUniformLocationNoCheck("light_pos"), 1, fake_light, 0);
		
		GLES20.glUniform4fv(_program.getUniformLocationNoCheck("diffuse_color"), 1,color, 0);
		
		GLES20.glUniform4fv(_program.getUniformLocationNoCheck("ambient_color"), 1, color, 0);
		
		//checkGlError("RenderModelTask.setGlobalUniformVariables");
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
