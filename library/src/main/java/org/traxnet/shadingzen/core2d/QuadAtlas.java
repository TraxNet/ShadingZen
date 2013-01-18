package org.traxnet.shadingzen.core2d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.rendertask.RenderTask;
import org.traxnet.shadingzen.rendertask.RenderTaskPool;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

/**
 * A QuadAtlas is a more optimized version of an sprite/quad as it packs many quads sharing 
 * the same texture into just one OpenGL call and texture lookup. Use QuadAtlas over Quad for 
 * clear performance reasons. 
 *
 * Usage:
 */
public class QuadAtlas extends Node2d {
	RenderBuffer _renderBuffer;
	ShadersProgram _program;
	TreeSet<Quad> _orderedQuads;
	int _maxQuads;
	boolean _needsBufferUpdate = false;
	BitmapTexture _texture;
	int _blendSrc, _blendDst;
	
	/*
	 * TODO: _renderBuffer no se libera y por tanto la memoria en OGL sigue ocupada. Deberia revisarse para todo en general.
	 */
	
	public QuadAtlas(){
		_blendSrc = GLES20.GL_SRC_ALPHA;
		_blendDst = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	}
	
	public void init(int max_quads, String texture_id, int texture_resource_id){
		_texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)this, texture_id, texture_resource_id, new BitmapTexture.Parameters());
		init(max_quads, _texture);
	}
	
	public void init(int max_quads, BitmapTexture texture){
		ShadersProgram program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "QuadAtlasShader", 0);   
		if(!program.isProgramDefined()){
			program.setName("QuadAtlasShader");
			program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_quadatlas_vertex));
			program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_quadatlas_fragment));
			program.setProgramsDefined();
		}
		
		this.internalInit(max_quads, texture, program);
	}
	
	public void initWithProgram(int max_quads, BitmapTexture texture, ShadersProgram program){
		registerResource(program);
		this.internalInit(max_quads, texture, program);
	}
	
	void internalInit(int max_quads, BitmapTexture texture, ShadersProgram program){
		_renderBuffer = (RenderBuffer) ResourcesManager.getSharedInstance().factory(RenderBuffer.class, (Entity)this, UUID.randomUUID().toString());
		_renderBuffer.init(max_quads*4*6, GLES20.GL_STATIC_DRAW, max_quads*6, GLES20.GL_STATIC_DRAW);
		//ResourcesManager.getSharedInstance().registerResource(_renderBuffer, this, null);
		
		_program = program;
        
		_maxQuads = max_quads;
		if((null == _texture ||(!_texture.getId().equals(texture.getId()))) && null != texture){
			_texture = texture;
			registerResource(texture);
		}
		
		
		_orderedQuads = new TreeSet<Quad>();
	}
	
	public void AddQuad(Quad quad){
		// Order quads comparing texture IDs
		_orderedQuads.add(quad);
		_needsBufferUpdate = true;
	}
	
	public void removeQuad(Quad quad){
		_orderedQuads.remove(quad);
		_needsBufferUpdate = true;
	}
	
	public void clearQuads(){
		_orderedQuads.clear();
	}
	
	/// Entity members ////////////////////////////////////


	@Override
	public void onDraw(RenderService renderer) {
		Iterator<Quad> iterator = _orderedQuads.descendingIterator();
		QuadRenderTask current_task = null;
		short verts_offset = 0, faces_offset = 0;
		
		current_task = (QuadRenderTask) RenderTaskPool.sharedInstance().newTask(QuadRenderTask.class);
		
		current_task.init(_texture, 0, _renderBuffer, this);
		current_task.setBlendingFunc(_blendSrc, _blendDst);
		current_task.enableBlending();
		current_task.disableDepthTest();
		current_task.setProgram(_program);
		
		while(true){
			if(!iterator.hasNext())
				break;
			
			// Iterate over all quads and create a render task for each run of quads with the same texture ID
			Quad next = iterator.next();
				
			// Increase counter for current task
			current_task.addQuad();
			
			if(_needsBufferUpdate){
				// Copy quad to render buffer
				faces_offset += next.fillElmentsArray(_renderBuffer.getElementBuffer(), faces_offset, (short)(verts_offset/5));
				verts_offset += next.fillVertsArray(_renderBuffer.getArrayBuffer(), verts_offset);
			}
			
		}
		
		current_task.setNeedsBufferUpdate(_needsBufferUpdate);
		renderer.addRenderTask(current_task);
		
		_needsBufferUpdate = false;
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
	}
	
	public void setBlendingFunc(int blend_src, int blend_dst){
		_blendSrc = blend_src;
		_blendDst = blend_dst;
	}
	
	
	
	public static class QuadRenderTask extends RenderTask {
		BitmapTexture _texture;
		RenderBuffer _buffer;
		Matrix4 _matrix = Matrix4.identity();
		int _offset, _numQuads;
		Node2d _owner;
		boolean _needsBufferUpdate = true;
		float [] mvp = new float[16];
		float [] ortho_matrix = new float[16];
		
		public QuadRenderTask(){}
		/*
		 * initializator for UIQuadRenderTask
		 * 
		 * @param texture The texture for this set of primitives
		 * @param quad_offset The offset inside the elements buffer (faces) from which we start rendering this set of quads
		 * @param buffer RenderBuffer with buffers for verts and elements
		 */
		public void init(BitmapTexture texture, int quad_offset, RenderBuffer buffer, Node2d owner){
			_texture = texture;
			_buffer = buffer;
			_offset = quad_offset;
			_numQuads = 0;
			_owner = owner; // TODO: remove!!
			_blendSrc = GLES20.GL_SRC_ALPHA;
			_blendDst = GLES20.GL_ONE_MINUS_SRC_ALPHA;
			_matrix.set(owner.getWorldModelMatrix());

            int viewport_w = Engine.getSharedInstance().getViewWidth();
			int viewport_h = Engine.getSharedInstance().getViewHeight();

			float view_factor_x = (float)viewport_w/(float)480;
			float view_factor_y = (float)viewport_h/(float)800;

            //ortho_matrix[0] = view_factor_x*2.f/viewport_w;
            ortho_matrix[0] = 2.f/viewport_w;
			ortho_matrix[1] = 0.f;
			ortho_matrix[2] = 0.f;
			ortho_matrix[3] = 0.f;
			
			ortho_matrix[4] = 0.f;
            //ortho_matrix[5] = view_factor_y*2.f/viewport_h;
            ortho_matrix[5] = 2.f/viewport_h;
			ortho_matrix[6] = 0.f;
			ortho_matrix[7] = 0.f;
			
			ortho_matrix[8] = 0.f;
			ortho_matrix[9] = 0.f;
			ortho_matrix[10] = 1.f;
			ortho_matrix[11] = 0.f;
			
			ortho_matrix[12] = -1.f;
			ortho_matrix[13] = -1.f;
			ortho_matrix[14] = 1.f;
			ortho_matrix[15] = 1.f;
		}
		
		public void setNumQuads(int num_quads){
			_numQuads = num_quads;
		}
		
		public void addQuad(){
			_numQuads++;
		}
		
		public RenderBuffer getRenderBuffer(){
			return _buffer;
		}
		
		public void setNeedsBufferUpdate(boolean value){
			_needsBufferUpdate = value;
		}

		@Override
		public void onDraw(RenderService service)  throws Exception {
			this.bindBlendingIfSet();
			this.bindDepthTestIfSet();
			

			service.setProgram(_program);
			setProgramUniforms();
			
			if(null != _texture)
				_texture.bindTexture(0);
			
			drawSubBuffer(_offset, _numQuads*2*3, GLES20.GL_TRIANGLES);
			
			if(null != _texture)
				_texture.unbindTexture(0);
			
			_program.unbindProgram();
			
			unbindBlendingIfSet();
			this.unbindDepthTestIfSet();
			
		}
		
		void setProgramUniforms() throws Exception {
			
			/*
			float [] ortho_matrix2 = {
					view_factor_x*2.f/viewport_w, 		   	0.f, 	0.f, 0.f,
                    0.f, view_factor_y*2.f/viewport_h, 		0.f, 	0.f,
                    0.f, 					  			 	0.f, 	1.f, 0.f,
                    -1.f, 					  				-1.f, 	1.f, 1.f
			};*/
			
			
			
			Matrix.multiplyMM(mvp, 0, ortho_matrix, 0, _matrix.getAsArray(), 0);
			
			GLES20.glUniform1f(_program.getUniformLocation("node_alpha"), this._owner.getNodeAlpha());
			
			GLES20.glUniform3f(_program.getUniformLocation("node_color"), this._owner.getNodeColor().getX(), this._owner.getNodeColor().getY(), this._owner.getNodeColor().getZ());
			// Move to the shaders as an uniform
			GLES20.glUniformMatrix4fv(_program.getUniformLocation("mvp_matrix"), 1, false, mvp, 0);
			
			GLES20.glUniform1i(_program.getUniformLocation("tex_unit"), 0);
		}
		
		
		
		void drawSubBuffer(int elements_offset, int elements_num, int mode){
			
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glEnableVertexAttribArray(2);
			_buffer.bindArrayBuffer();
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 5*4, 0);
			GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, 5*4, 3*4);
			
			
			// Draw the elements
			_buffer.bindElementBuffer();
                GLES20.glDrawElements(mode, elements_num, GLES20.GL_UNSIGNED_SHORT, elements_offset);
			
			
			// Revert back the state
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			GLES20.glDisableVertexAttribArray(0);
			GLES20.glDisableVertexAttribArray(2);
			
			
		}

		@Override
		public boolean onDriverLoad(Context context) {
			if(_needsBufferUpdate){
				_buffer.updateArrayBuffer();
				_buffer.updateElementBuffer();
				_needsBufferUpdate = false;
			}
			
			return true;
		}

		@Override
		public void initializeFromPool() {
			
			
		}

		@Override
		public void finalizeFromPool() {
			
			
		}
		
		
	}
}
