package com.example.shadingzen_helloworld;

import org.traxnet.shadingzen.core.Camera;
import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.core.GameInfo;
import org.traxnet.shadingzen.core.InputController;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.Scene;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;

public class HelloWorldGameInfo extends GameInfo implements InputController {
	protected Scene _gameScene;
	protected Camera _currentCamera;
	
	public HelloWorldGameInfo(){
		_gameScene = new Scene();
		Engine.getSharedInstance().pushScene(_gameScene);
		
		
		_currentCamera = new Camera();
		_currentCamera.setValues(new Vector3(0.0f, 0.0f, -8.0f), 1.5f, 4.0f/3.0f, 1.f, 200.0f);
		
        _currentCamera.setViewportSize(Engine.getSharedInstance().getViewWidth(), Engine.getSharedInstance().getViewHeight());
        
    	Engine.getSharedInstance().setCurrentCamera(_currentCamera);
    	Engine.getSharedInstance().getRenderService().setClearColor(new Vector4(0.0f, 0.0f, 0.0f, 0.f));
	}
	
	@Override
	public void onTouchDrag(float posx, float posy, float deltax, float deltay) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchUp(float posx, float posy) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScaleGesture(float scale_factor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGameStart() {
		_gameScene.spawn(TestCube.class, null, "TestCube01");
	}

	@Override
	public void onGameEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onUpdate(float deltaTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraw(RenderService renderer) throws Exception {
		// TODO Auto-generated method stub

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
