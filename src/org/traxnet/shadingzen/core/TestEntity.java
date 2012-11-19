package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.rendertask.RenderModelTask;



public class TestEntity extends Collider {
	OBJMesh _mesh;
	ShadersProgram _program;
	Renderer _openglRenderer;
	Texture _texture;
	float _rot;
	
	public TestEntity(){
		_mesh = (OBJMesh) ResourcesManager.getSharedInstance().factory(OBJMesh.class, this, "R.raw.inflated_cube", R.raw.inflated_cube);
		
		//_mesh = (Shape) ResourcesManager.getSharedInstance().factory(Sphere.class, "R.raw.mesh", R.raw.mesh);
		_program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "Shader1", 0);   
		_program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_vertex));
        _program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_fragment));
        _mesh.attachProgram(_program);
        Texture.Parameters params = new Texture.Parameters();
        _texture = (Texture) ResourcesManager.getSharedInstance().factory(Texture.class, (Entity)this, "Teture", R.raw.alpha_tex, params);
        
	}
	
	
	@Override
	public void onUpdate(float delta) {
		_rot += delta*0.01;
		
		//this._rotation.setRotation(new Vector3(0.f, 1.f, 0.f), _rot);
	}

	@Override
	public void onDraw(RenderService renderer) {
		RenderModelTask task = RenderModelTask.buildTask(_program, _mesh, this.getWorldModelMatrix(), _texture);
		
		renderer.addRenderTask(task);
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onUnload() {
		
	}
	
	@Override
	public BBox getBoundingBox(){
		Vector3 center = this._worldMatrix.mul(new Vector3());
		Vector3 extents = _mesh.getBoundingBox().getExtents();
		
		BBox ret = new BBox();
		ret.setCenterExtents(center, extents);
		
		return ret;
	}

	@Override
	public void onTouch(Collider other) {
		// TODO Auto-generated method stub
		
	}

}
