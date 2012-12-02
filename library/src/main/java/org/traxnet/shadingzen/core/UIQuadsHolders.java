package org.traxnet.shadingzen.core;

import java.util.Iterator;
import java.util.TreeSet;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.rendertask.RenderTask;
import org.traxnet.shadingzen.rendertask.RenderTaskPool;

import android.content.Context;
import android.opengl.GLES20;

public class UIQuadsHolders extends Entity {
	RenderBuffer _renderBuffer;
	ShadersProgram _program;
	TreeSet<UIQuad> _orderedQuads;
	int _maxQuads;
	
	public UIQuadsHolders(int max_quads){
		_renderBuffer = (RenderBuffer) ResourcesManager.getSharedInstance().factory(RenderBuffer.class, (Entity)this, null);
		_renderBuffer.init(max_quads*4*6, GLES20.GL_DYNAMIC_DRAW, max_quads*6, GLES20.GL_DYNAMIC_DRAW);
		/*_renderBuffer = new RenderBuffer(max_quads*4*6, GLES20.GL_DYNAMIC_DRAW, max_quads*4, GLES20.GL_DYNAMIC_DRAW);
		ResourcesManager.getSharedInstance().registerResource(_renderBuffer, this, "UIQuadsHolders");
		*/
		_program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "UIQuadsShader", 0);   
		_program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_uiquad_vertex));
        _program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_uiquad_fragment));
        
		_maxQuads = max_quads;
		
		_orderedQuads = new TreeSet<UIQuad>();
	}
	
	public void AddQuad(UIQuad quad){
		// Order quads comparing texture IDs
		_orderedQuads.add(quad);
	}
	
	public void ClearQuads(){
		_orderedQuads.clear();
	}
	
	/// Entity members ////////////////////////////////////

	@Override
	public void onTick(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDraw(RenderService renderer) {
		int current_texture = -1;
		Iterator<UIQuad> iterator = _orderedQuads.descendingIterator();
		UIQuadRenderTask current_task = null;
		short verts_offset = 0, faces_offset = 0;
		
		while(true){
			if(!iterator.hasNext())
				break;
			
			// Iterate over all quads and create a render task for each run of quads with the same texture ID
			UIQuad next = iterator.next();
			if(current_texture != next.getTexture().getTextureId()){
				// New texture ID found, create a new render task and send current one to renderer
				//if(null != current_task)
					//renderer.addRenderTask(current_task);
				
				current_task = (UIQuadRenderTask) RenderTaskPool.sharedInstance().newTask(UIQuadRenderTask.class);
				current_task.init(next.getTexture(), verts_offset, _renderBuffer);
				current_texture = next.getTexture().getTextureId();
				renderer.addRenderTask(current_task);
			} 
				
			// Increase counter for current task
			current_task.addQuad();
			
			
			// Copy quad to render buffer
			faces_offset += next.fillElmentsArray(_renderBuffer.getElementBuffer(), faces_offset, verts_offset);
			verts_offset += next.fillVertsArray(_renderBuffer.getArrayBuffer(), verts_offset);
			
		}
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
		
	}
	
	class UIQuadRenderTask extends RenderTask {
		BitmapTexture _texture;
		RenderBuffer _buffer;
		int _offset, _numQuads;
		
		public UIQuadRenderTask(){}
		
		/*
		 * Constructor for UIQuadRenderTask
		 * 
		 * @param texture The texture for this set of primitives
		 * @param quad_offset The offset inside the elements buffer (faces) from which we start rendering this set of quads
		 * @param buffer RenderBuffer with buffers for verts and elements
		 */
		public void init(BitmapTexture texture, int quad_offset, RenderBuffer buffer){
			_texture = texture;
			_buffer = buffer;
			_offset = quad_offset;
			_numQuads = 0;
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

		@Override
		public void onDraw(RenderService service) throws Exception {
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			GLES20.glDepthMask(false);
			
			
			// Activate program
			service.setProgram(_program);
			
			GLES20.glUniform1i(_program.getUniformLocation("tex_unit"), 0);
			
			// Send down uniforms
			setProgramUniforms();
			
			// Bind texture
			_texture.bindTexture(0);
			
			
			
			// draw buffer
			drawSubBuffer(_offset, _numQuads*2*3, GLES20.GL_TRIANGLES);
			
			_texture.unbindTexture(0);
			
			_program.unbindProgram();
			
		}
		
		void setProgramUniforms() throws Exception {
			int viewport_h = Engine.getSharedInstance().getViewHeight();
			int viewport_w = Engine.getSharedInstance().getViewWidth();
			
			float [] ortho_matrix = {
					2.f/viewport_w, 0.f, 0.f, -1.f,
                    0.f, 2.f/viewport_h, 0.f, -1.f,
                    0.f, 0.f, 1.f, 1.f,
                    0.f, 0.f, 0.f, 1.f
			};
			
				
			// Move to the shaders as an uniform
			GLES20.glUniformMatrix4fv(_program.getUniformLocation("ortho_matrix"), 1, false, ortho_matrix, 0);
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
			_buffer.updateArrayBuffer();
			_buffer.updateElementBuffer();
			
			return true;
		}

		@Override
		public void initializeFromPool() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void finalizeFromPool() {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}
