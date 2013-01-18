package org.traxnet.shadingzen.core;

import android.content.Context;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Particles extends Resource implements Shape {
	protected Parameters _params;
	protected boolean _isDriverDataDirty;
	protected int _num_vertices;
	protected boolean _updateBuffer;
	
	protected ShortBuffer _indicesBuffer;
	protected FloatBuffer _verticesBuffer;
	protected FloatBuffer _texcoordsBuffer;
	private IntBuffer _bufferIds;
	protected int _stride;

	public static class Parameters{
		int _num_particles;
		ParticleType _type;
		boolean _gen_textures;
		VertexAttribs _vertex_attribs;
		
		
		public enum ParticleType{
			POINT,
			QUAD
		}
		
		public enum VertexAttribs{
			POINT,
			POINT_FLOAT_POINT
		}
		
		public Parameters(){
			_num_particles = 0;
			_type = ParticleType.POINT;
			_gen_textures = false;
			_vertex_attribs = VertexAttribs.POINT;
		}
		
		public ParticleType getParticletype(){
			return _type;
		}
		public void setParticletype(ParticleType type){
			_type = type;
		}
		public int getNumParticles(){
			return _num_particles;
		}
		public void setNumParticles(int num){
			_num_particles = num;
		}
		public void setGenerateTextureCoords(boolean value){
			_gen_textures = value;
		}
		public boolean getGenerateTextureCoords(){
			return _gen_textures;
		}
		public void setVertexAttribs(VertexAttribs attribs){
			_vertex_attribs = attribs;
		}
		public VertexAttribs getVertexAttribs(){
			return _vertex_attribs;
		}
	}
	
	public Particles(){
		_isDriverDataDirty = true;
		_stride = 0;
		_lock = new Object();
		_updateBuffer = false;
	}
	
	/// Shape implementation
	@Override
	public void onDraw(RenderService service) {
		if(_isDriverDataDirty)
			return;
		
		if(_updateBuffer){
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _verticesBuffer.capacity()*4, _verticesBuffer, GLES20.GL_STATIC_DRAW);
			_updateBuffer = false;
		} else
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		
		if(Parameters.VertexAttribs.POINT_FLOAT_POINT ==  _params.getVertexAttribs()){
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glEnableVertexAttribArray(1);
			GLES20.glEnableVertexAttribArray(2);
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, _stride, 0);
			GLES20.glVertexAttribPointer(0, 1, GLES20.GL_FLOAT, false, _stride, 3);
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, _stride, 4);
		} else{
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
		}
		
		if(Parameters.ParticleType.POINT == _params.getParticletype())
			GLES20.glDrawArrays(GL10.GL_POINTS, 0, _params.getNumParticles());
		else{
			// TODO
		}
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		if(Parameters.VertexAttribs.POINT_FLOAT_POINT ==  _params.getVertexAttribs()){
			GLES20.glDisableVertexAttribArray(0);
			GLES20.glDisableVertexAttribArray(1);
			GLES20.glDisableVertexAttribArray(2);
		} else{
			GLES20.glDisableVertexAttribArray(0);
		}

		
		
	}
	
	/// Resource interface implementations

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id,
			Object params) {
		
		if(null != params)
			_params = (Parameters)params;
		else
			_params = new Parameters();
			
		
		if(Parameters.ParticleType.QUAD == _params.getParticletype()){
			_num_vertices = _params.getNumParticles()*4;
			_indicesBuffer = ShortBuffer.allocate(_params.getNumParticles()*6);  
			
			_verticesBuffer = FloatBuffer.allocate(_num_vertices*3);
			if(_params.getGenerateTextureCoords())
				_texcoordsBuffer = FloatBuffer.allocate(_num_vertices*2);
			
			for(int count=0; count < _params.getNumParticles(); count++){
				
			}
			
		} else{
			_num_vertices = _params.getNumParticles();
			switch(_params.getVertexAttribs()){
				case POINT_FLOAT_POINT:
					_stride = 7;
					_verticesBuffer = FloatBuffer.allocate(_num_vertices*_stride);
					break;
				case POINT:
				default:
					_stride = 0;
					_verticesBuffer = FloatBuffer.allocate(_num_vertices*3);
			}
			
		}
		
		
		
		
		
		_isDriverDataDirty = true;
		return false;
	}
	
	public FloatBuffer getInternalBuffer(){
		return _verticesBuffer;
	}

	@Override
	public boolean onDriverLoad(Context context) {
		// Store data into OpenGLES driver
		_bufferIds = ByteBuffer.allocateDirect(2 * Integer.SIZE/8).asIntBuffer();
		GLES20.glGenBuffers(2, _bufferIds);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, _verticesBuffer.capacity()*4, _verticesBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, 0 );
		
		/*
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, _ib.capacity()*2, _ib, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );	
		*/
		synchronized(_lock){
			_isDriverDataDirty = false;
		}
		return true;

	}

	@Override
	public boolean onResumed(Context context) {
		synchronized(_lock)
		{
			_isDriverDataDirty = true;
			return true;
		}
	}

	@Override
	public boolean onPaused(Context context) {
		return true;
	}

	@Override
	public boolean isDriverDataDirty() {
		synchronized(_lock){
			return _isDriverDataDirty;
		}
	}

	@Override
	public void onRelease() {
		// TODO Auto-generated method stub
		_bufferIds = null;
		_verticesBuffer = null;
	}


}
