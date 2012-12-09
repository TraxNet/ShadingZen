package com.example.shadingzen_helloworld;

import org.traxnet.shadingzen.core.EngineGLSurfaceView;
import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.core.GameInfo;
import org.traxnet.shadingzen.core.InputController;
import org.traxnet.shadingzen.core.Renderer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout;
import com.example.shadingzen_helloworld.R;

public class MainActivity extends Activity {
	EngineGLSurfaceView _surfaceView;
	Renderer _openglRenderer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.fullscreen_content);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);   
        
		_openglRenderer = new org.traxnet.shadingzen.core.Renderer(this);
        _surfaceView = new EngineGLSurfaceView(this, _openglRenderer);
        
        layout.addView(_surfaceView, params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	protected void loadGameInfo(){
		Engine engine = Engine.getSharedInstance(); // Singleton!
    	GameInfo gameInfo = new HelloWorldGameInfo();
		
		engine.pushInputController((InputController) gameInfo);
		gameInfo.onGameStart();
		engine.setGameInfo(gameInfo);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		loadGameInfo();
	}
}
