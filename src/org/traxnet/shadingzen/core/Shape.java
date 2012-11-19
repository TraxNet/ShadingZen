package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Matrix4;


public abstract class Shape extends Resource {
	protected Matrix4 _modelMatrix;
	protected ShadersProgram _program;
	
	public Shape(){
		_modelMatrix = new Matrix4();
		_program = null;
		_lock = new Object();
	}
	
	// This is most likely to be moved into an Entity object
	public void attachProgram(ShadersProgram program){
		synchronized(_lock){
			_program = program;
		}
	}
	
	public ShadersProgram getProgram(){
		synchronized(_lock){
			return _program;
		}
	}
	
	public abstract void onDraw(RenderService service);
	
	public void setModelMatrix(Matrix4 matrix){
		synchronized(_lock){
			_modelMatrix = matrix;
		}
	}
	
	public Matrix4 getModelMatrix(){
		synchronized(_lock){
			return _modelMatrix;
		}
	}
}
