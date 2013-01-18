package org.traxnet.shadingzen.rendertask;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.math.BBox;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class RenderBBoxTask extends RenderTask {
	BBox _bbox;
	Matrix4 _modelMatrix = new Matrix4();
	Vector4 _color = Vector4.zero();
	float [] _mvp = new float[16];
	float [] _mv = new float[16];
	
    // Vertices, Normals, Coords, Indices
    private IntBuffer _bufferIds;

    private ShortBuffer _ib;
    private FloatBuffer _mainb;
    
    public RenderBBoxTask(){}
	
	public void init(BBox box){
		_bbox = box;
		//_modelMatrix = modelMatrix;
		
		_program = Engine.getSharedInstance().getDebugWireframeProgram();
		
		int numVertices = 8;
		ArrayList<Float> mainBuffer = new ArrayList<Float>(numVertices * 6);
        ArrayList<Short> indicesB = new ArrayList<Short>(numVertices * 3);
        
        
        

        // Down box
        Vector3 vector = box.getMins();
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setX(box.getMaxs().getX());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setY(box.getMaxs().getY());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setX(box.getMins().getX());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
 
        // Up box
        vector = box.getMins();
        vector.setZ(box.getMaxs().getZ());
        
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setX(box.getMaxs().getX());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setY(box.getMaxs().getY());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        vector.setX(box.getMins().getX());
        mainBuffer.add(vector.getX());
        mainBuffer.add(vector.getY());
        mainBuffer.add(vector.getZ());
        
        
        // Lines
        indicesB.add((short)0);
        indicesB.add((short)1);
        indicesB.add((short)1);
        indicesB.add((short)2);
        indicesB.add((short)2);
        indicesB.add((short)3);
        indicesB.add((short)3);
        indicesB.add((short)0);
        
        indicesB.add((short)4);
        indicesB.add((short)5);
        indicesB.add((short)5);
        indicesB.add((short)6);
        indicesB.add((short)6);
        indicesB.add((short)7);
        indicesB.add((short)7);
        indicesB.add((short)4);
        
        indicesB.add((short)0);
        indicesB.add((short)4);
        indicesB.add((short)1);
        indicesB.add((short)5);
        indicesB.add((short)2);
        indicesB.add((short)6);
        indicesB.add((short)3);
        indicesB.add((short)7);
        
        
        float[] tempFloatBuffer = new float[mainBuffer.size()];
        
        // copy over the mainbuffer to the vertex + normal array
        for(int i = 0; i < mainBuffer.size(); i++)
        	tempFloatBuffer[i] = mainBuffer.get(i);
    
        
        short[] tempShortBuffer = new short[indicesB.size()];
        for(int i = 0; i < indicesB.size(); i++) {
        	tempShortBuffer[i] = indicesB.get(i);
        }
        
        _mainb = FloatBuffer.wrap(tempFloatBuffer);
		_ib = ShortBuffer.wrap(tempShortBuffer);
        
        
	}
	
	public Matrix4 getModelMatrix(){
		return _modelMatrix;
	}
	
	public void setColorRGBA(Vector4 c){
		_color.set(c);
	}
	public void setColorRGBA(float r, float g, float b, float a){
		_color.set(r, g, b, a);
	}

	@Override
	public void onDraw(RenderService service) throws Exception {
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthMask(false);
		
		Engine.getSharedInstance().getDebugWireframeProgram().bindProgram();
		
		setGlobalUniformVariables(service);
		
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 3*4, 0);
		
		
		// Draw the elements
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glDrawElements(GLES20.GL_LINES, _ib.capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		
		// Revert back the state
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(0);
		
		service.checkGlError("RenderBBoxTask draw");

		//GLES20.glDeleteBuffers(2, _bufferIds);
		
		_ib.clear();
	    _mainb.clear();
	}

	@Override
	public boolean onDriverLoad(Context context) {
		boolean has_failed = false;
		
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
                Log.e("ShadingZen", "RenderBBoxTask driver load" + ": glError " + error);
                has_failed = true;
        }
        
        
		if(_program.isDriverDataDirty())
			has_failed |= !_program.onDriverLoad((context));
			
		
		return !has_failed;
	}
	
	
	
	private void setGlobalUniformVariables(RenderService service) throws Exception {
		//Log.i("ShadingZen", "Setting global uniform variables for this program");
		
		
			
		// Calculate model-view-projection matrix
		/*Matrix.multiplyMM(_mv, 0, service.getViewMatrix(), 0, _modelMatrix.getAsArray(), 0);
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mv_matrix"), 1, false, _mv, 0);
		
		Matrix.multiplyMM(_mvp, 0, service.getProjectionMatrix(), 0, _mv, 0);
		// Move to the shaders as an uniform
		GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, _mvp, 0);
		
		GLES20.glUniformMatrix3fv(_program.getUniformLocation("normal_matrix"), 1, false, _modelMatrix.getAsArray3x3(), 0);  */
		
		//GLES20.glUniform3fv(_program.getUniformLocation("eye_point"), 1, service.getCameraPosition().getAsArray(), 0);
		GLES20.glUniform4f(_program.getUniformLocation("color"), _color.getX(), _color.getY(), _color.getZ(), _color.getW());

        Vector4 pos = _modelMatrix.mul(Vector4.zero());

        //GLES20.glUniform3f(_program.getUniformLocation("position"), pos.getX(), pos.getY(), pos.getZ());

        GLES20.glUniformMatrix4fv(_program.getUniformLocation("projection"), 1, false, service.getCamera().getViewProjectionMatrix().getAsArray(), 0);
		
		//checkGlError("RenderModelTask.setGlobalUniformVariables");
	}

	@Override
	public void initializeFromPool() {
		_color.set(0.f, 0.f, 0.f, 0.f);
		_blendDst = 0;
		_blendSrc = 0;
	}

	@Override
	public void finalizeFromPool() {
		_bufferIds.clear();
		_bufferIds = null;
		this._ib.clear();
		_ib = null;
		this._mainb.clear();
		_mainb = null;
		_bbox = null;
		
	}

}
