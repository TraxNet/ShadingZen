package org.traxnet.shadingzen.core;

import java.util.UUID;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core2d.Quad;
import org.traxnet.shadingzen.core2d.QuadAtlas;

/** 
 * @deprecated As of current state this class is deprecated and it will not work
 * @author Oscar
 *
 */
public class FadeSceneTransition extends SceneTransition {
	QuadAtlas _transitionQuad;
	float _halfTargetTime;
	
	public FadeSceneTransition(){}
	
	public void initWithScene(float time, Scene new_scene){
		super.initWithScene(time, new_scene);
		
		_halfTargetTime = time * 0.5f;
		
		_transitionQuad = (QuadAtlas) this._entityManager.spawn(QuadAtlas.class, null, "FadeSceneTransitionQuad" + UUID.randomUUID().toString());
		ShadersProgram program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "FadeSceneQuadProgram", 0);   
		if(!program.isProgramDefined()){
			program.setName("FadeSceneQuadProgram");
			program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_quadatlas_vertex));
			program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_quadatlas_fragment));
			program.setProgramsDefined();
		}
		_transitionQuad.initWithProgram(1, null, program);
		
		Quad quad = new Quad(0, 0, 0, Engine.getSharedInstance().getViewWidth(), Engine.getSharedInstance().getViewHeight(), 0, 0, 0, 0);
		_transitionQuad.AddQuad(quad);
		
		
	}

	@Override
	protected void onTransitionStep(float delta) {
		if(_currentTime < _halfTargetTime){
			_transitionQuad.setNodeAlpha(_currentTime/_halfTargetTime);
		} else{
			_transitionQuad.setNodeAlpha(1.f-(_targetTime - _currentTime)/_halfTargetTime);
		}
		
	}

	@Override
	protected boolean onTransitionStepDraw(RenderService renderer) {
		if(_currentTime < _halfTargetTime){
			_transitionQuad.setNodeAlpha(_currentTime/_halfTargetTime);
			_oldScene.onDraw(renderer);
			
			// _entityManager.drawHierarchy(renderer); // BROKEN CODE HERE ///////////////////////////////
		} else{
			_transitionQuad.setNodeAlpha((_targetTime - _currentTime)/_halfTargetTime);
			
			_newScene.onDraw(renderer);
			
			// _entityManager.drawHierarchy(renderer); // BROKEN CODE HERE ///////////////////////////////
		}
		
		return true;
		
	}

}
