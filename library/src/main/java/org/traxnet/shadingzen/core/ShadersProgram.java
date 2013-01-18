package org.traxnet.shadingzen.core;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *  Manages the generation and usage of vertex and fragment shaders.
 *  Once both shaders are compiled into a program, calling bindProgram sets its usage for the next rendering calls.
 *
 *  Usage:
 *      _program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "PieceShader1", 0);
 *      if(!_program.isProgramDefined()){
 *          _program.setName("PieceShader1");
 *          _program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_vertex));
 *          _program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_simple_fragment));
 *          _program.setProgramsDefined();
 *      }
 *
 *      During onDraw call _program.bindProgram to use it.
 */
public final class ShadersProgram extends Resource {
	private int _programId;
	private ArrayList<String> _VertexShaderSources;
	private ArrayList<String> _FragmetShaderSources;
	private String _programName = "NoProgramNae";
	private boolean _wasCompiled, _isDriverDataDirty;
	private Object _lock;
	private boolean _programIsDefined;
	private Hashtable<String, Integer> _attribLocations;
	
	public ShadersProgram(){
		_lock = new Object();
		_wasCompiled = false;
		_isDriverDataDirty = true;
		_programIsDefined = false;
		
		_VertexShaderSources = new ArrayList<String>();
		_FragmetShaderSources = new ArrayList<String>();
		_attribLocations = new Hashtable<String, Integer>();
	}
	
	public void setName(String name){
		_programName = name;
	}
	
	/** Creates the program using the attached shaders
	 * Must be called after all shaders have been attached to this program.
	 * @return True if the program has been successfully created and compiled.
	 */
	private boolean createProgram(){
		Log.v("ShadingZen", "Creating Shader Program '" + _programName + "' ...");
		_programId = GLES20.glCreateProgram();
        if (0 != _programId) {
        	Iterator<String> iter = _VertexShaderSources.iterator();
        	while(iter.hasNext()){
        		int shader = loadShader(GLES20.GL_VERTEX_SHADER, iter.next());
        		if(0 != shader)
        			GLES20.glAttachShader(_programId, shader);
        		else 
        			Log.i("ShadingZen", "ShadersProgram.createProgram: " + "Skipping shader.");
        		checkGlError("create program.attachVertexShader");
        	}
        	iter = _FragmetShaderSources.iterator();
        	while(iter.hasNext()){
        		int shader = loadShader(GLES20.GL_FRAGMENT_SHADER, iter.next());
        		if(0 != shader)
        			GLES20.glAttachShader(_programId, shader);
        		else 
        			Log.i("ShadingZen", "ShadersProgram.createProgram: " + "Skipping shader.");
        		checkGlError("create program.attachFragmentShader");
        	}

        	GLES20.glBindAttribLocation(_programId, 0, "v_position");
        	GLES20.glBindAttribLocation(_programId, 1, "v_normal");
        	GLES20.glBindAttribLocation(_programId, 2, "v_uv");
            GLES20.glBindAttribLocation(_programId, 3, "v_color");
            GLES20.glBindAttribLocation(_programId, 4, "v_tangent");
            //checkGlError("glAttachShader PS");
            GLES20.glLinkProgram(_programId);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(_programId, GLES20.GL_LINK_STATUS,
                    linkStatus, 0);
            if (GLES20.GL_TRUE != linkStatus[0]) {
                Log.e("ShadingZen", "Could not link _program: ");
                Log.e("ShadingZen", GLES20.glGetProgramInfoLog(_programId));
                GLES20.glDeleteProgram(_programId);
                _programId = 0;
                return false;
            }
            synchronized(_lock){
            	_isDriverDataDirty = false;
            }
            _wasCompiled = true;
            checkGlError("create program");
            return true;
        } else{
        	
            Log.d("ShadingZen", "Could not create program");
        	return false;
        }
	
	}
	
	/** Attach the given vertex shader source into this shader program
	 * 
	 * @param source The string to be compiled and loaded into OpenGL
	 */
	public void attachVertexShader(String source){
		Log.i("ShadingZen", "Attaching vertext shader:" + source);
		_VertexShaderSources.add(source);
	}
	
	/** Attach the given fragment shader source into this shader program
	 * 
	 * @param source The string to be compiled and loaded into OpenGL
	 */
	public void attachFragmentShader(String source){
		Log.i("ShadingZen", "Attaching fragment shader:" + source);
		_FragmetShaderSources.add(source);
	}
	
	public boolean isProgramDefined(){
		return _programIsDefined;
	}
	
	public void setProgramsDefined(){
		_programIsDefined = true;
	}
	
	
	/** Binds this program to the OpenGL context.
	 * Next drawing calls will use this set of vertex and 
	 * fragment shaders.
	 */
	public void bindProgram(){
		//checkGlError("prebind program " + this._programName);
		
		if(!_wasCompiled)
			createProgram();
		
		GLES20.glUseProgram(_programId);
	
		//checkGlError("bind program "  + this._programName);
	}
	
	public void unbindProgram(){
		GLES20.glUseProgram(0);
	
		//checkGlError("unbind program " + this._programName);
	}
	
	/** Returns this program id (used by the OpenGLES API)
	 * Return data is undefined until the program has been created 
	 * compiled using createProgram().
	 * @return This program ID
	 */
	public int getProgramId(){
		return _programId;
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
                Log.e("ShadingZen", "Could not compile shader "
                        + shaderType + ":");
                Log.e("ShadingZen", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
            
            checkGlError("load shader");
        }
        return shader;
    }
	
	void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", op + ": glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
              
        }
	 }

    /**
     * Given an attribute name, returns its OpenGL ID
     * @param attrib_name The attribute name
     * @return an OpenGL ID (integer > 1)
     * @throws Exception
     */
	public int getVertexAttribLocation(String attrib_name) throws Exception {
		Integer location = _attribLocations.get(attrib_name);
		if(null == location){
			location = GLES20.glGetAttribLocation(this._programId, attrib_name);
			if(-1 == location)
				throw new VertexAttributeLocationNotFound(this._programName, attrib_name);
			
			_attribLocations.put(attrib_name, location);
		}
		
		return location;
	}
	
	/** Returns the location for the give uniform variable name */ 
	public int getUniformLocation(String name) {
		Integer location = _attribLocations.get(name);
		if(null == location){
			location = GLES20.glGetUniformLocation(this._programId, name);
			if(-1 == location){
				Log.e("ShadingZen", "getUniformLocation: uniform location not found " + name);
				return -1;
			}
			
			_attribLocations.put(name, location);
		}
		
		return location;
	}
	
	public int getUniformLocationNoCheck(String name){
		Integer location = _attribLocations.get(name);
		if(null == location){
			location = GLES20.glGetUniformLocation(this._programId, name);
			if(-1 == location)
				return -1;
			
			_attribLocations.put(name, location);
		}
		
		return location;
	}
	
	
	/// Resource interface ////////////////////

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id, Object params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return createProgram();
	}

	@Override
	public boolean onResumed(Context context) {
		_isDriverDataDirty = true;
		return true;
	}

	@Override
	public boolean onPaused(Context context) {
		synchronized(_lock){
        	_isDriverDataDirty = true;
        	//GLES20.glDeleteProgram(_programId);
        }
		return true;
	}

	@Override
	public boolean isDriverDataDirty() {
		synchronized(_lock){
        	return _isDriverDataDirty;
        }
	}
	
	/** Release all memory from this resource 
	 * This will be called by the OpenGL thread when the resource is no longer needed
	 * by the logic thread and the OpenGL thread
	 */
	@Override
	public void onRelease(){
		GLES20.glDeleteProgram(_programId);
        Log.i("ShadingZen", "ShadersProgram: program deleted id=" + _programId + " name=" + _programName);
        _programId = 0;
	}
	
	
	public class VertexAttributeLocationNotFound extends Exception{
		
		
		private static final long serialVersionUID = 830774901258892183L;

		public VertexAttributeLocationNotFound(String program, String attribute_name){
			super(String.format("Vertex Attribute not found for shader program %s and attribute name %s", program, attribute_name));
		}
	}
	
	public class UniformLocationNotFound extends Exception{
		
		
		private static final long serialVersionUID = 830774901258892183L;

		public UniformLocationNotFound(String program, String name){
			super(String.format("Uniform Location not found for shader program %s and attribute name %s", program, name));
		}
	}
}
