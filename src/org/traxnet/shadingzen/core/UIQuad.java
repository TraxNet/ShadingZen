package org.traxnet.shadingzen.core;

public class UIQuad implements Comparable<Object>{
	float _x, _y;
	float _width, _height;
	float _u1, _u2, _v1, _v2;
	Texture _texture;
	
	/*
	 * UI Quad rendered as an orthogonal quad
	 * 
	 * @param texture Texture to be used for this quad
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param width Quad width
	 * @param height Quad height
	 * @param u1 Left U texture coordinate
	 * @param v1 Top V texture coordinate
	 * @param u2 Right U texture coordinate
	 * @param v2 Bottom V texture coordinate
	 */
	public UIQuad(Texture texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2){
		_x = x;
		_y = y;
		_width = width;
		_height = height;
		_u1 = u1;
		_v1 = v1;
		_u2 = u2;
		_v2 = v2;
		
		_texture = texture;
	}
	
	Texture getTexture(){
		return _texture;
	}
	
	/* Fill given array with verts data starting at offset param */
	public int fillVertsArray(float[] vertices, int offset){
		
		/*
		 
		  1 3
		  
		  0 2
		 
		 */
		
		vertices[offset + 0] = _x;
		vertices[offset + 1] = _y + _height;
		vertices[offset + 2] = 0.f;
		vertices[offset + 3] = _u1;
		vertices[offset + 4] = _v1;
		
		vertices[offset + 5] = _x;
		vertices[offset + 6] = _y;
		vertices[offset + 7] = 0.f;
		vertices[offset + 8] = _u1;
		vertices[offset + 9] = _v2;
		
		vertices[offset + 10] = _x + _width;
		vertices[offset + 11] = _y + _height;
		vertices[offset + 12] = 0.f;
		vertices[offset + 13] = _u2;
		vertices[offset + 14] = _v1;
		
		vertices[offset + 15] = _x + _width;
		vertices[offset + 16] = _y;
		vertices[offset + 17] = 0.f;
		vertices[offset + 18] = _u2;
		vertices[offset + 19] = _v2;
		
		return 20; // 20 new floats (4vets*5components)
	}
	
	public int fillElmentsArray(short[] elements, int offset, short verts_offset){
		elements[offset + 0] = (short) (verts_offset + 0);
		elements[offset + 1] = (short) (verts_offset + 1);
		elements[offset + 2] = (short) (verts_offset + 2);
		
		elements[offset + 3] = (short) (verts_offset + 2);
		elements[offset + 4] = (short) (verts_offset + 1);
		elements[offset + 5] = (short) (verts_offset + 3);
		
		return 6;
	}

	@Override
	public int compareTo(Object another) {
		if(UIQuad.class.isInstance(another)){
			UIQuad another_quad = (UIQuad)another;
			
			return another_quad._texture.getTextureId() - _texture.getTextureId();
		}
			
		return 0;
	}
}
