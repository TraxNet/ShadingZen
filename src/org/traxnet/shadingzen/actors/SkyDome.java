package org.traxnet.shadingzen.actors;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.Entity;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ResourcesManager;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.core.BitmapTexture;
import org.traxnet.shadingzen.core.BitmapTexture.TextureType;
import org.traxnet.shadingzen.exceptions.InvalidTextureType;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.rendertask.RenderTask;
import org.traxnet.shadingzen.shapes.CubeShape;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class SkyDome extends Actor {
	CubeShape _cubeShape;
	ShadersProgram _circleShapeProgram;
	RenderSkyDomeTask _task;
	BitmapTexture _texture;
	
	public void init() throws Exception {
		_circleShapeProgram = initSkyDomeProgram();
		
		_cubeShape = (CubeShape) resourceFactory(CubeShape.class, "CubeShape", 0);
		_cubeShape.initWithRaidus(10, _circleShapeProgram);
		
		_texture = loadCubeMap();
		
		_task = new RenderSkyDomeTask();
		_task.initWithCubemapTexture(_texture, _circleShapeProgram, _cubeShape, Matrix4.identity().getAsArray());	
	}
	
	BitmapTexture loadCubeMap(){
		BitmapTexture.Parameters params = new BitmapTexture.Parameters();
		params.setType(TextureType.TextureCubeMap);
		params.setCubeMapImage(0, R.raw.skydome1);
		params.setCubeMapImage(1, R.raw.skydome2);
		params.setCubeMapImage(2, R.raw.skydome3);
		params.setCubeMapImage(3, R.raw.skydome4);
		params.setCubeMapImage(4, R.raw.skydome5);
		params.setCubeMapImage(5, R.raw.skydome6);
		
		BitmapTexture texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)this, "SkyDomeTexture", R.raw.alpha_tex, new BitmapTexture.Parameters());
		return texture;
	}
	
	ShadersProgram initSkyDomeProgram(){
		ShadersProgram program = (ShadersProgram) resourceFactory(ShadersProgram.class, "SkyDomeProgram", 0);
		
		if(!program.isProgramDefined()){
			program.setName("SkyDomeProgram");
			program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_vertex));
        	program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_fragment));
        	program.setProgramsDefined();
		}
		
		return program;
	}
	
	@Override
	protected void onUpdate(float deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(RenderService renderer) throws Exception {
		renderer.addRenderTask(_task);
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub

	}
	
	class RenderSkyDomeTask extends RenderTask {
		CubeShape _shape;
		BitmapTexture _texture;
		boolean _needsBufferUpdate = true;
		float [] _modelMatrix;
		float [] _mvp = new float[16];
		float [] _mv = new float[16];
		
		public RenderSkyDomeTask(){
			
		}
		
		public void initWithCubemapTexture(BitmapTexture cubemap_texture, ShadersProgram program, CubeShape shape, float [] model_matrix) throws Exception {
			_shape = shape;
			_program = program;
			_texture = cubemap_texture;
			_modelMatrix = model_matrix;
			
			if(!_texture.isCubemap()){
				throw new InvalidTextureType();
			}
		}

		@Override
		public void onDraw(RenderService service) throws Exception {
			bindDepthTestIfSet();

			service.setProgram(_program);
		
			
			GLES20.glUniform1i(_program.getUniformLocation("tex_unit"), 0);
			setProgramUniforms(service);
			
			if(null != _texture)
				_texture.bindTexture(0);
			
			_shape.onDraw(service);
			
			if(null != _texture)
				_texture.unbindTexture(0);
			
			_program.unbindProgram();
			
		}

		@Override
		public boolean onDriverLoad(Context context) {
			if(_needsBufferUpdate){
				_shape.onDriverLoad(context);
				_needsBufferUpdate = false;
			}
			
			return true;
		}
		
		void setProgramUniforms(RenderService service) throws Exception {
			
			Matrix.multiplyMM(_mv, 0, service.getViewMatrix(), 0, _modelMatrix, 0);
			GLES20.glUniformMatrix4fv(_program.getUniformLocationNoCheck("mv_matrix"), 1, false, _mv, 0);
			
			Matrix.multiplyMM(_mvp, 0, service.getProjectionMatrix(), 0, _mv, 0);
			// Move to the shaders as an uniform
			GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, _mvp, 0);
			
			GLES20.glUniform1i(_program.getUniformLocationNoCheck("tex_unit"), 0);
			
		}
		
	}

}
