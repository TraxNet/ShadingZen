package org.traxnet.shadingzen;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


public class ShadingZenActivity extends Activity {
	GLSurfaceView _surfaceView;
	org.traxnet.shadingzen.Renderer _openglRenderer;
	
	/// ZONE: Activity overridden methods
	
    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
      
        _surfaceView = new ZenGLSurfaceView(this);
        _surfaceView.setEGLContextClientVersion(2);
        //_surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        _openglRenderer = new org.traxnet.shadingzen.Renderer(this);
        _surfaceView.setRenderer(_openglRenderer);
        
        
        loadDemoData();
        
        setContentView(_surfaceView);
    }
    
    /** Called when activity is going into backgrounds but not killed (yet) */
    @Override
    protected void onPause(){
    	_surfaceView.onPause();
    	super.onPause();
    }
    
    /** Called after onRestoreInstanceState(Bundle) */
    @Override
    protected void onResume(){
    	_surfaceView.onResume();
    	super.onResume();
    }
    
    /** Called when the activity is being re-initialized from a previously saved state */
    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState){
    	
    	
    }
    
    /** Called when activity should save its state prior to being paused or killed */
    @Override
    protected void onSaveInstanceState (Bundle outState){
    	
    }
    
    /// ZONE: private methods
    
    /** Loads some default demo data.
     * 
     */
    private void loadDemoData(){
    	// Load objects
        Shape mesh = new OBJMesh(R.raw.monkey, "obj1");
        mesh.onLoad(this);
        
        ShadersProgram program = new ShadersProgram("DefaultProgram");
        program.attachVertexShader(loadResourceString(R.raw.v_shader_simple));
        program.attachFragmentShader(loadResourceString(R.raw.f_shader_simple));
        program.createProgram();
        
        _openglRenderer.pushShape(mesh);
    }
    
    /** Given a resource ID, load it as an ASCII string.
     * Used to load vertex and fragment shader
     * @param id The resource id
     * @return The string representation of the resource, or null if not loaded
     */
    private String loadResourceString(int id){
		InputStream input_stream = getResources().openRawResource(id);
		
		//BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
		byte[] buffer = new byte[4096]; // WARNING!
		int read = 0;
		try {
			read = input_stream.read(buffer);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		String _ret = null;
		if(read > 0){
			_ret =  new String(buffer);
		} else{
			Log.e("loadResourceString"," Unalbe to load resource id=" + id);
		}
		
		return _ret;
	}
}
