package org.traxnet.shadingzen.core.actors;

import org.traxnet.shadingzen.core.Collider;
import org.traxnet.shadingzen.core.Model;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.Matrix4;

import android.opengl.GLES20;
import android.opengl.Matrix;

public abstract class Pawn extends Collider {
	protected Model _model;
	
	public Pawn(){
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onDraw(RenderService renderer) throws Exception {
		setGlobalUniformVariables(renderer);
		_model.onDraw(renderer);
	}

	private void setGlobalUniformVariables(RenderService renderer) throws Exception {
		//Log.i("ShadingZen", "Setting global uniform variables for this program");
		float [] mvp = new float[16];
		float [] mv = new float[16];
		Matrix4 rot = _rotation.toMatrix();
		
			
		// Calculate model-view-projection matrix
		Matrix.multiplyMM(mv, 0, renderer.getViewMatrix(), 0, rot.getAsArray(), 0);
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mv_matrix"), 1, false, mv, 0);
		
		Matrix.multiplyMM(mvp, 0, renderer.getProjectionMatrix(), 0, mv, 0);
		// Move to the shaders as an uniform
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, mvp, 0);
		
		GLES20.glUniformMatrix3fv(_program.getUniformLocation("normal_matrix"), 1, false, rot.getAsArray3x3(), 0);
		
		GLES20.glUniform3fv(_program.getUniformLocation("eye_point"), 1, renderer.getCamera().getPosition().getAsArray(), 0);
		
	}
}
