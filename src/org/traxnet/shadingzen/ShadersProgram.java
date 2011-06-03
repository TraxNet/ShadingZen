package org.traxnet.shadingzen;

import java.util.ArrayList;
import java.util.Iterator;


import android.opengl.GLES20;
import android.util.Log;

public final class ShadersProgram {
	private int _programId;
	private ArrayList<String> _VertexShaderSources;
	private ArrayList<String> _FragmetShaderSources;
	private String _programName;
	private boolean _wasCompiled;
	
	public ShadersProgram(String name){
		_programName = name;
		_wasCompiled = false;
		
		_VertexShaderSources = new ArrayList<String>();
		_FragmetShaderSources = new ArrayList<String>();
	}
	
	/** Creates the program using the attached shaders
	 * Must be called after all shaders have been attaced to this program.
	 * @return True if the program has been successfully created and compiled.
	 */
	public boolean createProgram(){
		_programId = GLES20.glCreateProgram();
        if (0 != _programId) {
        	Iterator<String> iter = _VertexShaderSources.iterator();
        	while(iter.hasNext()){
        		int shader = loadShader(GLES20.GL_VERTEX_SHADER, iter.next());
        		if(0 != shader)
        			GLES20.glAttachShader(_programId, shader);
        		else 
        			Log.i("ShadersProgram.createProgram", "ShadersProgram.createProgram: " + "Skipping shader.");
        	}
        	iter = _FragmetShaderSources.iterator();
        	while(iter.hasNext()){
        		int shader = loadShader(GLES20.GL_FRAGMENT_SHADER, iter.next());
        		if(0 != shader)
        			GLES20.glAttachShader(_programId, shader);
        		else 
        			Log.i("ShadersProgram.createProgram", "ShadersProgram.createProgram: " + "Skipping shader.");
        	}
            //checkGlError("glAttachShader PS");
            GLES20.glLinkProgram(_programId);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(_programId, GLES20.GL_LINK_STATUS,
                    linkStatus, 0);
            if (GLES20.GL_TRUE != linkStatus[0]) {
                Log.e("Shader", "Could not link _program: ");
                Log.e("Shader", GLES20.glGetProgramInfoLog(_programId));
                GLES20.glDeleteProgram(_programId);
                _programId = 0;
                return false;
            }
            _wasCompiled = true;
            return true;
        } else{
            Log.d("CreateProgram", "Could not create program");
        	return false;
        }
	
	}
	
	/** Attach the given vertex shader source into this shader program
	 * 
	 * @param source The string to be compiled and loaded into OpenGL
	 */
	public void attachVertexShader(String source){
		_VertexShaderSources.add(source);
	}
	
	/** Attach the given fragment shader source into this shader program
	 * 
	 * @param source The string to be compiled and loaded into OpenGL
	 */
	public void attachFragmentShader(String source){
		_FragmetShaderSources.add(source);
	}
	
	
	/** Binds this program to the OpenGL context.
	 * Next drawing calls will use this set of vertex and 
	 * fragment shaders.
	 */
	public void bindProgram(){
		if(!_wasCompiled)
			createProgram();
		
		GLES20.glUseProgram(_programId);
	
	}
	
	/** Returns this program id (used by the OpenGLES API)
	 * Return data is undefined until the program has been created 
	 * compiled using createProgram().
	 * @return This program ID
	 */
	public int getProgramId(){
		return _programId;
	}
	
	/** Returns the location for the give uniform variable name */ 
	public int getUniformLocation(String name){
		return GLES20.glGetUniformLocation(_programId, name);
	}
	
	private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,
                    compiled, 0);
            if (0 == compiled[0]) {
                Log.e("Shader", "Could not compile shader "
                        + shaderType + ":");
                Log.e("Shader", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
}
