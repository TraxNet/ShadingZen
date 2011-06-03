/*
 * OBJ fileformat mesh shape
 * 
 * OBJ file loading based on the project adroidshaders at:
 * 	http://code.google.com/p/androidshaders/source/browse/src/graphics/shaders/Mesh.java?name=shadowmap
 */

package org.traxnet.shadingzen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;

public class OBJMesh extends Shape {
	private int resourceId;
	
	// Constants
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int SHORT_SIZE_BYTES = 2;
    // the number of elements for each vertex
    // [coordx, coordy, coordz, normalx, normaly, normalz....]
    private final int VERTEX_ARRAY_SIZE = 8;
    
    // if tex coords exist
    private final int VERTEX_TC_ARRAY_SIZE = 8;

    // Vertices
    private float _vertices[];

    // Normals
    private float _normals[];
    
    // Texture coordinates
    private float _texCoords[];
    
    // Indices
    private short _indices[];
    
    // MainBuffer interleaved
    private float _mainBuffer[];
    
    // Vertices, Normals, Coords, Indices
    IntBuffer _bufferIds;

    // Buffers - index, vertex, normals and texcoords
    private FloatBuffer _vb;
    private FloatBuffer _nb;
    private ShortBuffer _ib;
    private FloatBuffer _mainb;
    private FloatBuffer _tcb;

    // Normals
    private float[] _faceNormals;
    private int[]   _surroundingFaces; // # of surrounding faces for each vertex
 
    private int _normalsBufferId, _textCoordsBufferId;
	
	public OBJMesh(int resource_id, String id){
		super(id);
		resourceId = resource_id;
	}

	@Override
	public void onLoad(Context context) {
		try{
			InputStream input_stream = context.getResources().openRawResource(resourceId);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
		
			loadOBJ(reader);
			
			_mainb = FloatBuffer.wrap(_mainBuffer);
			_ib = ShortBuffer.wrap(_indices);
			
			// Store data into OpenGLES driver
			_bufferIds = IntBuffer.allocate(2);
			GLES20.glGenBuffers(_bufferIds.capacity(), _bufferIds);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _vertices.length*8*Float.SIZE, _mainb, GLES20.GL_STATIC_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _indices.length*Short.SIZE, _ib, GLES20.GL_STATIC_DRAW);
			
		} catch(Exception e){
			
		}
		
	}

	/** Handle the actual call to the OpenGLES DrawElements.
	 * First, we setup the data pointers and enable the required states
	 * We then draw all elements and restore back the default state
	 */
	@Override
	public void onDraw(GL10 gl) {
		// Enable vertex attribute pointers and set the data offset for each one
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glEnableVertexAttribArray(1);
		GLES20.glEnableVertexAttribArray(2);
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, 0, 3);
		GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, 0, 6);
		
		// Bind each attribute to a program attribute variable
		// this is fixed for use, we always need to set v_position
		// v_normal and v_uv for all programs
		int program = 0;
		GLES20.glBindAttribLocation(program, 0, "v_position");
		GLES20.glBindAttribLocation(program, 1, "v_normal");
		GLES20.glBindAttribLocation(program, 2, "v_uv");
		
		// Draw the elements
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, _ib.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		
		// Revert back the state
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(0);
		GLES20.glDisableVertexAttribArray(1);
		GLES20.glDisableVertexAttribArray(2);
	}
	
	private int loadOBJ(BufferedReader in) throws Exception {
        try {
                //Log.d("In OBJ:", "First");
                /* read vertices first */
                String str = in.readLine();
                StringTokenizer t = new StringTokenizer(str);
                
                String type = t.nextToken();
                
                // keep reading vertices
                int numVertices = 0;
                ArrayList<Float> vs = new ArrayList<Float>(100); // vertices
                ArrayList<Float> tc = new ArrayList<Float>(100); // texture coords
                ArrayList<Float> ns = new ArrayList<Float>(100); // normals
                
                while(type.equals("v")) {
                        //Log.d("In OBJ:", "V: " + str);
                        
                        vs.add(Float.parseFloat(t.nextToken()));        // x
                        vs.add(Float.parseFloat(t.nextToken()));        // y
                        vs.add(Float.parseFloat(t.nextToken()));        // z
                
                        // next vertex
                        str = in.readLine();
                        t = new StringTokenizer(str);
                        
                        type = t.nextToken();
                        numVertices++;
                }
                
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
                
                
                // create the vertex buffer
                _vertices = new float[numVertices * 3];
                // create the normal buffer
                _normals = new float[numVertices * 3];
                // texcoord
                _texCoords = new float[numTexCoords * 2];
                            
                _mainBuffer = new float[numVertices * 8]; 
                // copy over data - INEFFICIENT [SHOULD BE A BETTER WAY]
                for(int i = 0; i < numVertices; i++) {
                	_vertices[i * 3]        = vs.get(i * 3);
                	_vertices[i * 3 + 1] = vs.get(i * 3 + 1);
                	_vertices[i * 3 + 2] = vs.get(i * 3 + 2);
                        
                	_normals[i * 3 ]      = -ns.get(i * 3);
                	_normals[i * 3 + 1] = -ns.get(i * 3 + 1);
                	_normals[i * 3 + 2] = -ns.get(i * 3 + 2);
                        
                        // transfer tex coordinates
                    if (i < numTexCoords) {
                           _texCoords[i * 2]         = tc.get(i * 2);
                           _texCoords[i * 2 + 1] = tc.get(i * 2 + 1);
                    }
                    
                    _mainBuffer[i*8]   = _vertices[i*3];
                    _mainBuffer[i*8+1] = _vertices[i*3+1];
                    _mainBuffer[i*8+2] = _vertices[i*3+2];
                    
                    _mainBuffer[i*8+3] = _normals[i*3];
                    _mainBuffer[i*8+4] = _normals[i*3+1];
                    _mainBuffer[i*8+5] = _normals[i*3+2];
                    
                    _mainBuffer[i*8+6] = _texCoords[i*2];
                    _mainBuffer[i*8+7] = _texCoords[i*2+1];
                }
       
                
                // now read all the faces
                String fFace, sFace, tFace;
                ArrayList<Float> mainBuffer = new ArrayList<Float>(numVertices * 6);
                ArrayList<Short> indicesB = new ArrayList<Short>(numVertices * 3);
                StringTokenizer lt, ft; // the face tokenizer
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
                                        
                                        // Add all the vertex info
                                        mainBuffer.add(_vertices[vert * 3]);    // x
                                        mainBuffer.add(_vertices[vert * 3 + 1]);// y
                                        mainBuffer.add(_vertices[vert * 3 + 2]);// z
                                
                                        // add the normal info
                                        mainBuffer.add(_normals[vertN * 3]);    // x
                                        mainBuffer.add(_normals[vertN * 3 + 1]); // y
                                        mainBuffer.add(_normals[vertN * 3 + 2]); // z
                                        
                                        // add the tex coord info
                                        mainBuffer.add(_texCoords[texc * 2]);     // u
                                        mainBuffer.add(_texCoords[texc * 2 + 1]); // v
                                        
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
                /*
                _vertices = new float[mainBuffer.size()];
                
                // copy over the mainbuffer to the vertex + normal array
                for(int i = 0; i < mainBuffer.size(); i++)
                        _vertices[i] = mainBuffer.get(i);*/
                
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
	
}
