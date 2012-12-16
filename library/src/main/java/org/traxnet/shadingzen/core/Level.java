package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.BitmapTexture.TextureType;

import android.opengl.GLES20;
import android.opengl.Matrix;

public abstract class Level extends Entity {
	
	static Level _globalCurrentLevel;
	
	static Level getCurrent(){
		return _globalCurrentLevel;
	}
	static void setCurrent(Level current){
		_globalCurrentLevel = current;
	}
	
	
	boolean _useSkyDome;
	SkyDomeActor _skyDomeActor;
	
	public Level(){
		_useSkyDome = false;
	}
	

	@Override
	public void onTick(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(RenderService renderer) {
		// TODO Auto-generated method stub

	}
	
	public void setSkydome(){
		_skyDomeActor = new SkyDomeActor();
		_skyDomeActor.load();
		_useSkyDome = true;
	}
	
	void renderSkydrome(RenderService renderer) throws Exception{
		if(!_useSkyDome)
			return;
		
		_skyDomeActor.drawDome(renderer);
	}
	
	
	class SkyDomeActor extends Actor{
		Sphere _skydomeSphere;
		ShadersProgram _skydomeProgram;
		BitmapTexture _skydomeTexture;
		
		public SkyDomeActor(){
			
		}

		@Override
		public void onUpdate(float delta) {
			// TODO Auto-generated method stub

		}
		
		public void drawDome(RenderService renderer) throws Exception{
			if(_skydomeProgram.isDriverDataDirty())
				_skydomeProgram.onDriverLoad(renderer.getContext());
			if(_skydomeTexture.isDriverDataDirty())
				_skydomeTexture.onDriverLoad(renderer.getContext());
			if(_skydomeSphere.isDriverDataDirty())
				_skydomeSphere.onDriverLoad(renderer.getContext());
			
			GLES20.glDisable(GLES20.GL_CULL_FACE);
			GLES20.glDepthMask(false);
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			
			
			
			_skydomeProgram.bindProgram();
			_skydomeTexture.bindTexture(0);
			GLES20.glUniform1i(_skydomeProgram.getUniformLocation("tex_skydrome"), 0);
			
			float [] model = new float[16];
			float [] mv = new float[16];
			Matrix.setIdentityM(model, 0);
			//Matrix.rotateM(model, 0, this._yaw*50, 0.f, 1.f, 0.f);
			//Matrix.rotateM(model, 0, this._roll*50, 0.f, 0.f, -1.f);
			//Matrix.translateM(model, 0, _currentCamera.getPosition().getX(), _currentCamera.getPosition().getY(), _currentCamera.getPosition().getZ());

			Matrix.multiplyMM(mv, 0, renderer.getViewMatrix(), 0, model, 0);
			GLES20.glUniformMatrix4fv(_skydomeProgram.getUniformLocation("mv_matrix"), 1, false, mv, 0);
			
			GLES20.glUniformMatrix4fv(_skydomeProgram.getUniformLocation("p_matrix"), 1, false, renderer.getProjectionMatrix(), 0);
			
			_skydomeSphere.onDraw(renderer);
			
			_skydomeProgram.unbindProgram();
			_skydomeTexture.unbindTexture(0);
		}
		
		public void load(){
			_skydomeSphere = (Sphere)ResourcesManager.getSharedInstance().factory(Sphere.class, this, "SkyDomeScene");
			_skydomeProgram = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, this, "SkyDomeProgram");
			_skydomeProgram.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_skydome_vertex));
			_skydomeProgram.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_skydome_fragment));
			_skydomeSphere.attachProgram(_skydomeProgram);
			BitmapTexture.Parameters params = new BitmapTexture.Parameters();
			params.setType(TextureType.TextureCubeMap);
			params.setCubeMapImage(0, R.raw.skydome1);
			params.setCubeMapImage(1, R.raw.skydome2);
			params.setCubeMapImage(2, R.raw.skydome3);
			params.setCubeMapImage(3, R.raw.skydome4);
			params.setCubeMapImage(4, R.raw.skydome5);
			params.setCubeMapImage(5, R.raw.skydome6);
			_skydomeTexture = (BitmapTexture)ResourcesManager.getSharedInstance().factory(BitmapTexture.class, this, "SkyDomeTexture", 0, params);
			
		}

		@Override
		public void onDraw(RenderService renderer) throws Exception{
			renderSkydrome(renderer);
			
		}

		@Override
		public void onLoad() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUnload() {
			// TODO Auto-generated method stub
			
		}
	}

}
