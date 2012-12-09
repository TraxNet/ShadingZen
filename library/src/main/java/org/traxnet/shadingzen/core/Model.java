package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.BBox;
import android.opengl.GLES20;

public class Model extends Entity {
	ShadersProgram _program;
	BitmapTexture _texture;
	Shape _shape;
	BBox _bbox;
	
	public Model(){
		_bbox = new BBox();
	}

	@Override
	public void onTick(float delta) {
		// TODO Auto-generated method stub
		

	}
	

	@Override
	public void onDraw(RenderService renderer) throws Exception{
		renderer.setProgram(_program);
	
		
		GLES20.glUniform1i(_program.getUniformLocation("tex_unit"), 0);
		//setGlobalUniformVariables();
		
		_texture.bindTexture(0);
		
		_shape.onDraw(renderer);

	}

	@Override
	public void onLoad() {
		

	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub

	}
	
	public void setShadersProgram(ShadersProgram program){
		_program = program;
	}
	public ShadersProgram getShadersProgram(){
		return _program;
	}

	public void setTexture(BitmapTexture texture){
		_texture = texture;
	}
	public BitmapTexture getTexture(){
		return _texture;
	}
	
	public void setShape(Shape shape){
		_shape = shape;
	}
	public Shape getShape(){
		return _shape;
	}
	
	public BBox getBoundingBox(){
		return _bbox;
	}
}
