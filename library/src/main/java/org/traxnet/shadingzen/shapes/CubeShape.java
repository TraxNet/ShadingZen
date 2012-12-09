package org.traxnet.shadingzen.shapes;

import org.traxnet.shadingzen.core.RenderBuffer;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.core.Shape;
import org.traxnet.shadingzen.exceptions.ShadersProgramNotFoundException;

import android.content.Context;
import android.opengl.GLES20;

public class CubeShape extends Shape {
	RenderBuffer _renderBuffer;
	
	public static int VertexDataSize = 5;
	
	public CubeShape(){
		
	}
	
	// Cube Triangle Strip: 4 3 7 8 5 3 1 4 2 7 6 5 2 1
	/***
	 * Initializes the cube center at zero and sides at +radius and -radius
	 * @param radius
	 */
	public void initWithRaidus(float radius, ShadersProgram program) throws Exception {
		_renderBuffer = new RenderBuffer();
		_program = program;
		
		if(null == _program)
			throw new ShadersProgramNotFoundException();
		
		_renderBuffer.init(8*VertexDataSize, GLES20.GL_STATIC_DRAW, 14, GLES20.GL_STATIC_DRAW);
		
		buildCube(radius);
	}
	
	void buildCube(float radius){
		float [] verts = _renderBuffer.getArrayBuffer();
		
		int vert = 0;
		
		//Up Box
		verts[vert*VertexDataSize + 0] = radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = -radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = -radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		
		// Down Box
		verts[vert*VertexDataSize + 0] = radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = -radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = -radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize + 0] = radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		
		// 4 3 7 8 5 3 1 4 2 7 6 5 2 1
		short [] elements = _renderBuffer.getElementBuffer();
		elements[0] = 4;
		elements[1] = 3;
		elements[2] = 7;
		elements[3] = 8;
		elements[4] = 5;
		elements[5] = 3;
		elements[6] = 1;
		elements[7] = 4;
		elements[8] = 2;
		elements[9] = 7;
		elements[10] = 6;
		elements[11] = 5;
		elements[12] = 2;
		elements[13] = 1;
	}

	@Override
	public void onDraw(RenderService service) {
		drawSubBuffer(0, _renderBuffer.getElementBuffer().length, GLES20.GL_TRIANGLE_STRIP);

	}
	
	void drawSubBuffer(int elements_offset, int elements_num, int mode){
		int index0 = GLES20.glGetAttribLocation(_program.getProgramId(), "v_position");
		int index1 = GLES20.glGetAttribLocation(_program.getProgramId(), "v_uv");

		//checkGlError("drawSubBuffer post glGetAttribLocation");
					
		GLES20.glEnableVertexAttribArray(index0);
		GLES20.glEnableVertexAttribArray(index1);
		//checkGlError("drawSubBuffer post glEnableVertexAttribArray index0="+index0);
		
		_renderBuffer.bindArrayBuffer();
		GLES20.glVertexAttribPointer(index0, 3, GLES20.GL_FLOAT, false, VertexDataSize*4, 0); // Position
		GLES20.glVertexAttribPointer(index0, 2, GLES20.GL_FLOAT, false, VertexDataSize*4, 3*4); // Position
		
		// Draw the elements
		_renderBuffer.bindElementBuffer();
		GLES20.glDrawElements(mode, elements_num, GLES20.GL_UNSIGNED_SHORT, elements_offset);
		
		
		// Revert back the state
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(index0);
		GLES20.glDisableVertexAttribArray(index1);
		//checkGlError("drawSubBuffer post glDisableVertexAttribArray index0="+index0);
		
	}

	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id,
			Object params) {
		
		
		return true;
	}

	@Override
	public boolean onDriverLoad(Context context) {
		return _renderBuffer.onDriverLoad(context);
	}

	@Override
	public boolean onResumed(Context context) {
		return _renderBuffer.onDriverLoad(context);
	}

	@Override
	public boolean onPaused(Context context) {
		return _renderBuffer.onPaused(context);
	}

	@Override
	public void onRelease() {
		_renderBuffer.onRelease();

	}

	@Override
	public boolean isDriverDataDirty() {
		return _renderBuffer.isDriverDataDirty();
	}

}
