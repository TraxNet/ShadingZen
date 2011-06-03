package org.traxnet.shadingzen;

import javax.microedition.khronos.opengles.GL10;
import java.lang.String;
import android.content.Context;

public abstract class Shape {
	protected Vector3 _position;
	protected String _id;
	protected ShadersProgram _program;
	
	public Shape(String id){
		_position = new Vector3(0.f, 0.f, 0.f);
		_id = id;
		_program = null;
	}
	
	// This is most likely to be moved into an Entity object
	public void attachProgram(ShadersProgram program){
		_program = program;
	}
	
	public ShadersProgram getProgram(){
		return _program;
	}
	
	public abstract void onLoad(Context context);
	
	public abstract void onDraw(GL10 gl);
	
	public Vector3 getPosition(){
		return _position;
	}
	public void setPosition(Vector3 v){
		_position = v;
	}
	public String getId(){
		return _id;
	}
	
}
