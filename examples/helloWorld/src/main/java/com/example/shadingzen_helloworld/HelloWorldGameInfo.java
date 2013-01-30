package com.example.shadingzen_helloworld;

import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;

public class HelloWorldGameInfo extends GameInfo implements InputController {
	protected Scene _gameScene;
	protected Camera _currentCamera;
    protected float _time = 0.f;
    protected float _domeSize = 600.f;
	
	public HelloWorldGameInfo() throws Exception{
		_gameScene = new Scene();

       /* _gameScene.init(_domeSize, R.raw.skydome1, R.raw.skydome2,
                R.raw.skydome3, R.raw.skydome4, R.raw.skydome5, R.raw.skydome6);  */

		Engine.getSharedInstance().pushScene(_gameScene);
		
		
		_currentCamera = new Camera();
		_currentCamera.setValues(new Vector3(0.0f, 0.0f, -8.0f), 1.5f, 4.0f/3.0f, 1.f, 1100.f);
		
        _currentCamera.setViewportSize(Engine.getSharedInstance().getViewWidth(), Engine.getSharedInstance().getViewHeight());
        
    	Engine.getSharedInstance().setCurrentCamera(_currentCamera);
    	Engine.getSharedInstance().getRenderService().setClearColor(new Vector4(0.0f, 0.0f, 0.3f, 0.f));
	}
	
	@Override
	public boolean onTouchDrag(int pointer_id,float posx, float posy, float deltax, float deltay) {
		return true;
	}

	@Override
	public boolean onTouchUp(int pointer_id,float posx, float posy) {
		return true;
	}

    @Override
    public boolean onTouchDown(int pointer_id, float posx, float posy) {
        return true;
    }

    @Override
	public boolean onScaleGesture(float scale_factor) {
		return true;
	}

	@Override
	public void onGameStart() {
		_gameScene.spawn(TestCube.class, null, "TestCube01");
	}

	@Override
	public void onGameEnd() {
	}

	@Override
	protected void onUpdate(float deltaTime) {
        _time += deltaTime;

        float x = (float)(8*Math.cos(_time));
        float y =    (float)(8*Math.sin(_time));
        float z =  (float)(8*Math.cos(_time*0.7f));
        _currentCamera.setPosition(x, z, y);
        _currentCamera.setDirection(-x, -z, -y);

	}

	@Override
	public void onDraw(RenderService renderer) throws Exception {
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void onUnload() {

	}

}
