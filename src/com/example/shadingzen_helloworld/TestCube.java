package com.example.shadingzen_helloworld;

import java.util.UUID;

import org.traxnet.shadingzen.core.Actor;
import org.traxnet.shadingzen.core.BitmapTexture;
import org.traxnet.shadingzen.core.Entity;
import org.traxnet.shadingzen.core.OBJMesh;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.Renderer;
import org.traxnet.shadingzen.core.ResourcesManager;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.rendertask.RenderModelTask;

import android.util.FloatMath;


public class TestCube extends Actor {
	OBJMesh _mesh;
	ShadersProgram _program;
	Renderer _openglRenderer;
	BitmapTexture _texture;
	float _time = 0.f;
	
	public TestCube(){
		_mesh = (OBJMesh) ResourcesManager.getSharedInstance().factory(OBJMesh.class, this, "cubletable_"+UUID.randomUUID().toString(), R.raw.inflated_cube2);


		_program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "PieceShader1", 0);   
		if(!_program.isProgramDefined()){
			_program.setName("PieceShader1");
			_program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_vertex));
        	_program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_fragment));
        	_program.setProgramsDefined();
		}
        _mesh.attachProgram(_program);
        BitmapTexture.Parameters params = new BitmapTexture.Parameters();
        _texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)this, "cubletableTexture_"+UUID.randomUUID().toString(), R.raw.cubetext02, params);
	}

	@Override
	protected void onUpdate(float deltaTime) {
		_time += deltaTime;

	}

	@Override
	public void onDraw(RenderService renderer) throws Exception {
		// Send down a render task to the renderer. This RenderTask handles rendering of OBJMeshes
		RenderModelTask task = RenderModelTask.buildTask(_program, _mesh, this.getWorldModelMatrix(), _texture);
		
		float r = FloatMath.cos(_time);
		float g = FloatMath.sin(_time*0.4f);
		task.setDiffuseColor(r, g, 0.3f, 1.f);
		task.setAmbientColor(0.2f, 0.2f, 0.2f, 0.f);

		renderer.addRenderTask(task);

	}

	@Override
	public void onLoad() {
	

	}

	@Override
	public void onUnload() {
		

	}

}