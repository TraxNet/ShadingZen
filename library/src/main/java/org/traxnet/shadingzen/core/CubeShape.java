package org.traxnet.shadingzen.core;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class CubeShape extends Resource implements Shape {
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
        _renderBuffer.setId(_id+"_renderbuffer");

		
		_renderBuffer.init(8*VertexDataSize, GLES20.GL_STATIC_DRAW, 14, GLES20.GL_STATIC_DRAW);
		
		buildCube(radius);
	}
	
	void buildCube(float radius){
		float [] verts = _renderBuffer.getArrayBuffer();
		
		int vert = 0;


		verts[vert*VertexDataSize] = -radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = -radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = -radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;

		verts[vert*VertexDataSize] = -radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = radius;
		verts[vert*VertexDataSize + 1] = radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = -radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		vert++;
		verts[vert*VertexDataSize] = radius;
		verts[vert*VertexDataSize + 1] = -radius;
		verts[vert*VertexDataSize + 2] = radius;
		verts[vert*VertexDataSize + 3] = 0.f;
		verts[vert*VertexDataSize + 4] = 0.f;
		
		// 4 3 7 8 5 3 1 4 2 7 6 5 2 1
        // 4-3-2-1-5-3-7-4-8-2-6-5-8-7
		short [] elements = _renderBuffer.getElementBuffer();
		elements[0] = 3;
		elements[1] = 2;
		elements[2] = 1;
		elements[3] = 0;
		elements[4] = 4;
		elements[5] = 2;
		elements[6] = 6;
		elements[7] = 3;
		elements[8] = 7;
		elements[9] = 1;
		elements[10] =5;
		elements[11] =4;
		elements[12] =7;
		elements[13] = 6;
	}

	@Override
	public void onDraw(RenderService service) {
        try{
		    drawSubBuffer(service, 0, _renderBuffer.getElementBuffer().length, GLES20.GL_TRIANGLE_STRIP);
        } catch (Exception ex){
            Log.e("ShadingZen", "Unable to render CubeShape:" + ex.getMessage(), ex);
        }

	}
	
	void drawSubBuffer(RenderService service, int elements_offset, int elements_num, int mode) throws Exception{


		//int index0 = _program.getVertexAttribLocation("v_position");
		//int index1 = _program.getVertexAttribLocation("v_uv");

        int index0 = 0;
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_CULL_FACE);


					
		GLES20.glEnableVertexAttribArray(index0);

		//GLES20.glEnableVertexAttribArray(index1);
        //service.checkGlError("drawSubBuffer post glEnableVertexAttribArray index1="+index1);
		
		_renderBuffer.bindArrayBuffer();
		GLES20.glVertexAttribPointer(index0, 3, GLES20.GL_FLOAT, false, VertexDataSize*4, 0); // Position
		//GLES20.glVertexAttribPointer(index0, 2, GLES20.GL_FLOAT, false, VertexDataSize*4, 3*4); // Position
		
		// Draw the elements
		_renderBuffer.bindElementBuffer();
		GLES20.glDrawElements(mode, elements_num, GLES20.GL_UNSIGNED_SHORT, elements_offset);

        //GLES20.glEnable( GLES20.GL_CULL_FACE );

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glCullFace(GLES20.GL_BACK);

		// Revert back the state
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(index0);

		//GLES20.glDisableVertexAttribArray(index1);
        //service.checkGlError("drawSubBuffer post glDisableVertexAttribArray index1="+index1);
		
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
