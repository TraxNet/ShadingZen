package org.traxnet.shadingzen.core2d;

import java.util.TreeSet;
import java.util.UUID;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.Engine;
import org.traxnet.shadingzen.core.Entity;
import org.traxnet.shadingzen.core.RenderBuffer;
import org.traxnet.shadingzen.core.ResourcesManager;
import org.traxnet.shadingzen.core.ShadersProgram;
import org.traxnet.shadingzen.core.font.BMFont;
import org.traxnet.shadingzen.core.font.CharDescriptor;
import org.traxnet.shadingzen.math.Vector4;

import android.opengl.GLES20;
import android.util.Log;

/*
 * Label can render a text string with the given BMFont file.
 * 
 * Note: Current only one page is support, this the BMFont can only have one texture with
 * 		 all characters inside.
 */
public class Label extends QuadAtlas {
	Vector4 _color;
	BMFont _font;
	
	public Label(){
		_color = new Vector4(1.f, 1.f, 1.f, 1.f);
	}
	
	public void initWithStringAndFont(String text, int resource_id, int texture_res){
		initWithCapacityAndFont(text.length(), resource_id, texture_res);
		
		generateQuadsForText(text);
	}
	
	public void initWithCapacityAndFont(int capacity, int resource_id, int texture_res){
		
		allocateNewRenderBufferWithCapacity(capacity);
		
		/*_renderBuffer = new RenderBuffer(text.length()*4*6, GLES20.GL_STATIC_DRAW, text.length()*6, GLES20.GL_STATIC_DRAW);
		ResourcesManager.getSharedInstance().registerResource(_renderBuffer, this, null);
		*/
		_program = (ShadersProgram)ResourcesManager.getSharedInstance().factory(ShadersProgram.class, (Entity)this, "LabelShader", 0);
		if(!_program.isProgramDefined()){
			_program.setName("LabelShader");
			_program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_quadatlas_vertex));
        	_program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_fontrender_fragment));
        	_program.setProgramsDefined();
		}
		
		
		_orderedQuads = new TreeSet<Quad>();
		
		loadFont(resource_id, texture_res);	
		
		_texture = _font.getPage(0).getTexture();
	}
	
	
	
	void allocateNewRenderBufferWithCapacity(int num_quads){
		if(null != _renderBuffer)
			this.removeResource(_renderBuffer);
		
		_maxQuads = Math.max(num_quads, 32);
		_renderBuffer = (RenderBuffer) ResourcesManager.getSharedInstance().factory(RenderBuffer.class, (Entity)this, UUID.randomUUID().toString());
		_renderBuffer.init(_maxQuads*4*6, GLES20.GL_STATIC_DRAW, _maxQuads*6, GLES20.GL_STATIC_DRAW);
	}
	
	void loadFont(int resource_id, int texture_res){
		_font = new BMFont();
		if(!_font.loadFromResource(Engine.getSharedInstance().getContext(), resource_id, texture_res)){
			Log.e("ShadingZen", "Unable to load font for id:" + resource_id);
		}
	}
	
	public void rebuildText(String new_text)
	{
		if(new_text.length() > _maxQuads){
			new_text = new_text.substring(0, _maxQuads);
			
			//allocateNewRenderBufferWithCapacity(new_text.length());
		}
		
		generateQuadsForText(new_text);
	}
	
	void generateQuadsForText(String text){
		clearQuads();
		
		float current_x = 0.f;
		float height = 0.f;
	    for ( int i = 0; i < text.length(); ++i ) {
	    	short ascii_val = (short)text.charAt( i );
	        
	    	CharDescriptor char_desc = _font.getChar(ascii_val);
	    	if(null == char_desc)
	    		continue;
	    	
	    	Quad q = new Quad(
	    			i, (float)(current_x + char_desc.getXOffset()),
	    			//(float)char_desc.getYOffset(),
	    			0.f,
	    			(float)char_desc.getWidth(),
	    			(float)char_desc.getHeight(),
	    			(float)char_desc.getX() / (float)_font.getCharset().getWidth(),
	    			(float)(_font.getCharset().getHeight() - char_desc.getY() )/ (float)_font.getCharset().getHeight(),
	    			(float)(char_desc.getX() + char_desc.getWidth())/ (float)_font.getCharset().getWidth(),
	    			(float)( _font.getCharset().getHeight() - (char_desc.getY()+ char_desc.getHeight()) )/ (float)_font.getCharset().getHeight()
	    			);
	    	
	    	height = Math.max(height, char_desc.getHeight());
	    	current_x += char_desc.getXAdvance();
	    	this.AddQuad(q);
	     }
	    
	    this.setContentSize(current_x, height);
	    
	    _needsBufferUpdate = true;
	}
	
	public void setColor(Vector4 color){
		_color.set(color);
	}
	
	public void setColor(float r, float g, float b, float a){
		_color.set(r, g, b, a);
	}
	/*
	@Override
	public void onDraw(RenderService renderer) {
		
	}*/
}
