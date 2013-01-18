/*
 * OBJ fileformat mesh shape
 * 
 * OBJ file loading based on the project adroidshaders at:
 * 	http://code.google.com/p/androidshaders/source/browse/src/graphics/shaders/Mesh.java?name=shadowmap
 */

package org.traxnet.shadingzen.core;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Vector3;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class OBJMesh extends Resource implements Shape {
	private int resourceId;
	private String _id;
	private boolean _isDataDirty = true;
    // Indices
    private short _indices[];
    
    // MainBuffer interleaved
    private float _mainBuffer[];
    
    // Vertices, Normals, Coords, Indices
    private IntBuffer _bufferIds;

    // Buffers - index, vertex, normals and texcoords
    private ShortBuffer _ib;
    private FloatBuffer _mainb;
    
    protected BBox _bbox;
    protected float _scale = 1.f;
	
	public OBJMesh(){

	}

	/** Handle the actual call to the OpenGLES DrawElements.
	 * First, we setup the data pointers and enable the required states
	 * We then draw all elements and restore back the default state
	 */
	@Override
	public void onDraw(RenderService service) {
		//Log.i("ShadingZen", "Drawing OBJMesh shape..");
		// Enable vertex attribute pointers and set the data offset for each one
		
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glEnableVertexAttribArray(1);
		GLES20.glEnableVertexAttribArray(2);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 8*4, 0);
		GLES20.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, 8*4, 3*4);
		GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, 8*4, 6*4);
		
		// Bind each attribute to a program attribute variable
		// this is fixed for use, we always need to set v_position
		// v_normal and v_uv for all programs
		
		// Draw the elements
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, _ib.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		
		// Revert back the state
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(0);
		GLES20.glDisableVertexAttribArray(1);
		GLES20.glDisableVertexAttribArray(2);
		
		service.checkGlError("OBJMesh draw");
		
		//Log.i("ShadingZen", "OBJMesh shape sent for drawing");
	}
	
	public BBox getBoundingBox(){
		return _bbox;
	}
	
	private int loadOBJ(BufferedReader in, float scale) throws Exception {
        try {
        		Vector3 mins = new Vector3(99999, 99999, 99999);
        		Vector3 maxs = new Vector3(-99999, -99999, -99999);
                //Log.d("In OBJ:", "First");
                /* read vertices first */
                String str = in.readLine();
                while(str.startsWith("#"))
                	str = in.readLine();
                while(str.startsWith("mtllib"))
                	str = in.readLine();
                while(str.startsWith("o"))
                	str = in.readLine();
            	
                StringTokenizer t = new StringTokenizer(str);
                
                String type = t.nextToken();
                
                // keep reading vertices
                int numVertices = 0;
                ArrayList<Float> vs = new ArrayList<Float>(100); // vertices
                ArrayList<Float> tc = new ArrayList<Float>(100); // texture coords
                ArrayList<Float> ns = new ArrayList<Float>(100); // normals
                
                
                
                while(type.equals("v")) {
                        //Log.d("In OBJ:", "V: " + str);
                	float x = Float.parseFloat(t.nextToken())*scale;
                	float y = Float.parseFloat(t.nextToken())*scale;
                	float z = Float.parseFloat(t.nextToken())*scale;
                        
                        vs.add(x);        // x
                        vs.add(y);        // y
                        vs.add(z);        // z
                        
                        if(x < mins.getX())
                        	mins.setX(x);
                        if(x > maxs.getX())
                        	maxs.setX(x);
                        
                        if(y < mins.getY())
                        	mins.setY(y);
                        if(y > maxs.getY())
                        	maxs.setY(y);
                        
                        if(z < mins.getZ())
                        	mins.setZ(z);
                        if(z > maxs.getZ())
                        	maxs.setZ(z);
                
                        // next vertex
                        str = in.readLine();
                        t = new StringTokenizer(str);
                        
                        type = t.nextToken();
                        numVertices++;
                }
                
                _bbox = new BBox(mins, maxs);
                
                // read tex coords
                int numTexCoords = 0;
                if (type.equals("vt")) {
                        while(type.equals("vt")) {
                                tc.add(Float.parseFloat(t.nextToken()));        // u
                                tc.add(Float.parseFloat(t.nextToken()));        // v
                        
                                // next texture coord
                                str = in.readLine();
                                t = new StringTokenizer(str);
                                
                                type = t.nextToken();
                                numTexCoords++;
                        }
                }
                
                // read vertex normals
                if (type.equals("vn")) {
                        while(type.equals("vn")) {
                                ns.add(Float.parseFloat(t.nextToken()));        // x
                                ns.add(Float.parseFloat(t.nextToken()));        // y
                                ns.add(Float.parseFloat(t.nextToken()));        // y
                                
                                // next texture coord
                                str = in.readLine();
                                t = new StringTokenizer(str);
                                
                                type = t.nextToken();
                        }
                }
                
                if(type.equals("usemtl")){
                	while(str.startsWith("usemtl"))
                		str = in.readLine();
                	t = new StringTokenizer(str);
                	type = t.nextToken();
                }
                if(type.equals("s")){
                	while(str.startsWith("s"))
                		str = in.readLine();
                	t = new StringTokenizer(str);
                	type = t.nextToken();
                }
                
                // now read all the faces
                String fFace;
                ArrayList<Float> mainBuffer = new ArrayList<Float>(numVertices * 6);
                ArrayList<Short> indicesB = new ArrayList<Short>(numVertices * 3);
                StringTokenizer ft; // the face tokenizer
                int numFaces = 0;
                short index = 0;
                if (type.equals("f")) {
                        while (type.equals("f")) {
                                // Each line: f v1/vt1/vn1 v2/vt2/vn2 
                                // Figure out all the vertices
                                for (int j = 0; j < 3; j++) {
                                        fFace = t.nextToken();
                                        // another tokenizer - based on /
                                        ft = new StringTokenizer(fFace, "/");
                                        int vert = Integer.parseInt(ft.nextToken()) - 1;
                                        int texc = Integer.parseInt(ft.nextToken()) - 1;
                                        int vertN = Integer.parseInt(ft.nextToken()) - 1;
                                        
                                        // Add to the index buffer
                                        indicesB.add(index++);
                                        //indicesB.add((short)vert);
                                        
                                        // Add all the vertex info
                                        mainBuffer.add(vs.get(vert * 3));    // x
                                        mainBuffer.add(vs.get(vert * 3 + 1));// y
                                        mainBuffer.add(vs.get(vert * 3 + 2));// z
                                
                                        // add the normal info
                                        mainBuffer.add(-ns.get(vertN * 3));    // x
                                        mainBuffer.add(-ns.get(vertN * 3 + 1)); // y
                                        mainBuffer.add(-ns.get(vertN * 3 + 2)); // z
                                   
                                        // add the tex coord info
                                        if(texc < numTexCoords){
                                        	mainBuffer.add(tc.get(texc * 2));     // u
                                        	mainBuffer.add(tc.get(texc * 2 +1 )); // v
                                        }
                                        
                                }
                                
                                // next face
                                str = in.readLine();
                                if (str != null) {
                                        t = new StringTokenizer(str);
                                        numFaces++;
                                        type = t.nextToken();
                                }
                                else
                                        break;
                        }
                }
                
                //Log.d("COMPLETED MAINBUFFER:", "" + mainBuffer.size());
              
                _mainBuffer = new float[mainBuffer.size()];
                
                // copy over the mainbuffer to the vertex + normal array
                for(int i = 0; i < mainBuffer.size(); i++)
                	_mainBuffer[i] = mainBuffer.get(i);
                
                //Log.d("COMPLETED TRANSFER:", "VERTICES: " + _vertices.length);
                
                // copy over indices buffer
                indicesB.trimToSize();
                
                _indices = new short[indicesB.size()];
                for(int i = 0; i < indicesB.size(); i++) {
                        _indices[i] = indicesB.get(i);
                }
                
                _ib = ShortBuffer.wrap(_indices);
                
                return 1;
                
        } catch(Exception e) {
                throw e;
        }
	}
	
	/// Resource interface implementation //////////////////////////////

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id, Object params) {
		try{
			resourceId = resource_id;
			_id = id;
			
			InputStream input_stream = context.getResources().openRawResource(resource_id);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
		
			loadOBJ(reader, _scale);
			
			_mainb = FloatBuffer.wrap(_mainBuffer);
			_ib = ShortBuffer.wrap(_indices);
			
			Log.i("ShadingZen", "----> OBJ Mesh data");
			Log.i("ShadingZen", "		Loaded " + _mainBuffer.length/8 + " vertices");
			Log.i("ShadingZen", "		Loaded " + _indices.length + " indices");
			return true;
		} catch(Exception e){
			Log.e("ShadingZen", "Unable to read OBJ mesh with id " + _id + ":" + e.getLocalizedMessage());
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
			Log.e("ShadingZen", sw.toString());
			return false;
		}
	}

	@Override
	public boolean onDriverLoad(Context context) {
		// Store data into OpenGLES driver
		_bufferIds = ByteBuffer.allocateDirect(2 * Integer.SIZE/8).asIntBuffer();
		GLES20.glGenBuffers(2, _bufferIds);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _mainb.capacity()*4, _mainb, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, 0 );
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, _ib.capacity()*2, _ib, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
		
		int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", "OBJMesh driver load" + ": glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
        }

		_isDataDirty = false;

		return true;
	}

    @Override
	public boolean onResumed(Context context) {
		return true;
	}

    @Override
	public boolean onPaused(Context context) {
        _isDataDirty = true;
        GLES20.glDeleteBuffers(2, _bufferIds);
        return true;

	}

    @Override
	public boolean isDriverDataDirty()
    {
		return _isDataDirty;

	}
	
	/** Release all memory from this resource 
	 * This will be called by the OpenGL thread when the resource is no longer needed
	 * by the logic thread and the OpenGL thread
	 */
    @Override
	public void onRelease(){
	    // TODO: remove data from OpenGL driver
	    _ib.clear();
	    _mainb.clear();
	}
	
	
}
