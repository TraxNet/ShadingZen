package org.traxnet.shadingzen.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import android.content.Context;

public class BitmapTexture extends Resource implements Texture {
	protected IntBuffer _textureIds;
	protected int _width, _height;
	protected boolean _driverDataDirty;
	protected boolean _hasAlpha;
	protected boolean _isWatingForRelease;
	protected Parameters _params;
	protected int _target;
	protected Bitmap [] _bmps;
	
	public enum TextureType{
		Texture2D,
		TextureCubeMap
	}
	public enum TextureFilter{
		Nearest,
		Linear,
		Bilinear
	}
	
	public static class Parameters{
		TextureType _type;
		int [] _cubemap;
		TextureFilter _magFilter, _minFilter;
		boolean _genMipmaps;
		
		
		public Parameters(){
			_type = TextureType.Texture2D;
			_cubemap = new int[6];
			_magFilter = TextureFilter.Nearest;
			_minFilter = TextureFilter.Nearest;
			_genMipmaps = true;
		}
		
		public void setType(TextureType type){
			_type = type;
		}
		
		public TextureType getType(){
			return _type;
		}
		
		public int getCubeMapImage(int num){
			return _cubemap[num];
		}
		
		public TextureFilter getMinFilter(){
			return _minFilter;
		}
		public void setMinFilter(TextureFilter filter){
			_minFilter = filter;
		}
		public TextureFilter getMagFilter(){
			return _magFilter;
		}
		public void setMagFilter(TextureFilter filter){
			_magFilter = filter;
		}
		
		public void setCubeMapImage(int num, int resource_id){
			if(6 <= num || 0 > num)
				return;
			_cubemap[num] = resource_id;
		}
		public void setGenMipMaps(boolean mode)
		{
			_genMipmaps = mode;
		}
		public boolean getGenMipMaps(){
			return _genMipmaps;
		}
	}
	
	public BitmapTexture(){
	}
	
	
	int calculateUpperPowerOfTwo(int v)
	{
	    v--;
	    v |= v >>> 1;
	    v |= v >>> 2;
	    v |= v >>> 4;
	    v |= v >>> 8;
	    v |= v >>> 16;
	    v++;
	    return v;

	}
	
	boolean isPowerOfTwo(int i){
		return ( i & (i - 1)) == 0;
	}
	
	public boolean isCubemap(){
		return _target == GLES20.GL_TEXTURE_CUBE_MAP;
	}
	
	
	boolean loadAsTexture2D(Context context, String id, int resource_id, BitmapTexture.Parameters params){
		_bmps = new Bitmap[1];
		Matrix flip = new Matrix();
	    flip.postScale(1f, -1f);
	    
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inScaled = false;
		Bitmap textureBmp = BitmapFactory.decodeResource(context.getResources(), resource_id, opts);
		
		if(Engine.getSharedInstance().resizeTexturesToPowerOfTwo && (!isPowerOfTwo(textureBmp.getWidth()) || !isPowerOfTwo(textureBmp.getHeight()))){
			int target_width = calculateUpperPowerOfTwo(textureBmp.getWidth());
			int target_height = calculateUpperPowerOfTwo(textureBmp.getHeight());
			
			Log.i("ShadingZen", "Texture id=" + id + " has no power of two dimesions " + textureBmp.getWidth() + "x" + textureBmp.getHeight() + " adjusting to " + target_width + "x" + target_height);
			
			Bitmap temp =  Bitmap.createBitmap(textureBmp, 0, 0, textureBmp.getWidth(), textureBmp.getHeight(), flip, false);
			_bmps[0] = Bitmap.createScaledBitmap(temp, target_width, target_height, false);
			temp.recycle();
		} else{
			_bmps[0]  = Bitmap.createBitmap(textureBmp, 0, 0, textureBmp.getWidth(), textureBmp.getHeight(), flip, false);
		}
		
	    textureBmp.recycle();
		
		_hasAlpha = textureBmp.hasAlpha();
		_width = _bmps[0].getWidth();
		_height = _bmps[0].getHeight();
		_driverDataDirty = true;
		_params = params;
		_target = GLES20.GL_TEXTURE_2D;
		return true;
	}
	
	boolean loadAsTextureCubeMap(Context context, String id, int resource_id, BitmapTexture.Parameters params){
		_bmps  = new Bitmap[6];
		Matrix flip = new Matrix();
	    flip.postScale(1f, -1f);
	    
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inScaled = false;
	    
		for(int side=0; side < 6; side++){
			Bitmap textureBmp = BitmapFactory.decodeResource(context.getResources(), params.getCubeMapImage(side), opts);
		    _bmps[side]  = Bitmap.createBitmap(textureBmp, 0, 0, textureBmp.getWidth(), textureBmp.getHeight(), flip, false);
		    textureBmp.recycle();
			_hasAlpha = textureBmp.hasAlpha();
			_width = textureBmp.getWidth();
			_height = textureBmp.getHeight();
		}
		_params = params;
		_driverDataDirty = true;
		_target = GLES20.GL_TEXTURE_CUBE_MAP;
		return true;
	}
	
	/// Resource implementations
	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id, Object params) {
		try{
			if(null != params){
				BitmapTexture.Parameters texparams = (BitmapTexture.Parameters)params;
				if(texparams.getType() == TextureType.Texture2D)
					return loadAsTexture2D(context, id, resource_id, texparams);
				else
					return loadAsTextureCubeMap(context, id, resource_id, texparams);
					
			}
			
			return loadAsTexture2D(context, id, resource_id, new BitmapTexture.Parameters());
		} catch(Exception e){
			return false;
		}
	}

	@Override
	public boolean onDriverLoad(Context context) {
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		if(TextureType.Texture2D == _params.getType()){
			_textureIds = IntBuffer.allocate(1);
			GLES20.glGenTextures(1, _textureIds);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureIds.get(0)); 
			if(_hasAlpha)
				//GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, _width, _height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, _buffer);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, _bmps[0], 0);
			else
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, _bmps[0], 0);
				//GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB565, _bmps[0], 0);
				//GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, _width, _height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, _buffer);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR); 
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			
			if(_params.getGenMipMaps())
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
			
		} else{
			_textureIds = IntBuffer.allocate(1);
			GLES20.glGenTextures(1, _textureIds);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, _textureIds.get(0));
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			//GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GL10.GL_TEXTUR,GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR); 
			if(_hasAlpha){
				for(int side=0; side<6; side++){
					GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, _bmps[side], 0);
					//GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP, 0, GLES20.GL_RGBA, _width, _height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, _buffer);
				}
			}
			else{
				for(int side=0; side<6; side++){
					GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + side, 0, _bmps[side], 0);
					//GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP, 0, GLES20.GL_RGB565, _bmps[side], 0);
					//GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP, 0, GLES20.GL_RGB565, _width, _height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, _buffer);
				}
				
			}
			
			if(_params.getGenMipMaps())
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_CUBE_MAP);
		}
		
		_driverDataDirty = false;
		
		return true;
	}

	@Override
	public boolean onResumed(Context context) {
		_driverDataDirty = true;
		return true;
	}

	@Override
	public boolean onPaused(Context context) {
		_driverDataDirty = true;
		return true;
	}

	@Override
	public boolean isDriverDataDirty() {
		return _driverDataDirty;
	}

	@Override
	public void onRelease() {
		GLES20.glDeleteTextures(1, _textureIds);
		_bmps = null;
	    _textureIds = null;   
	}

	
	/// Texture public methods
	
	/* Returns the id for this texture in OGL ES. Must be called after the texture has been loaded into the driver memory */
	public int getTextureId(){
		return _textureIds.get(0);
	}
	
	public void bindTexture(int unit){
		//if(_driverDataDirty)
		//	onDriverLoad(null); // WARNING!!!!!!!!!!!! null
		
		if(null == _bmps || null == _textureIds){
			_driverDataDirty = true;
			return;
		}
			
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
		GLES20.glBindTexture(_target, _textureIds.get(0));
		
		//checkGlError("bind texture");
	}
	
	public void unbindTexture(int unit){	
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
		GLES20.glBindTexture(_target, 0);
		
	}
	
	void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e("ShadingZen", op + ": glError " + error);
                //throw new RuntimeException(op + ": glError " + error);
        }
	 }
	
	
	/// Protected methods
	
	private ByteBuffer extract(Bitmap bmp) 
	{ 
		int channels = 3;
		if(bmp.hasAlpha())
			channels = 4;
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * channels); 
		bb.order(ByteOrder.BIG_ENDIAN); 
		IntBuffer ib = bb.asIntBuffer(); 
		// Convert ARGB -> RGBA 
		if(bmp.hasAlpha()){
			for (int y = bmp.getHeight() - 1; y > -1; y--) 
			{ 
		
				for (int x = 0; x < bmp.getWidth(); x++) 
				{ 
					int pix = bmp.getPixel(x, bmp.getHeight() - y - 1); 
					int alpha = ((pix >> 24) & 0xFF); 
					int red = ((pix >> 16) & 0xFF); 
					int green = ((pix >> 8) & 0xFF); 
					int blue = ((pix) & 0xFF); 
				
					// Make up alpha for interesting effect 
				
					//ib.put(red << 24 | green << 16 | blue << 8 | ((red + blue + green) / 3)); 
					ib.put(red << 24 | green << 16 | blue << 8 | alpha); 
				} 
			} 
		} else{
			for (int y = 0; y < bmp.getHeight(); y++) 
			{ 
		
				for (int x = 0; x < bmp.getWidth(); x++) 
				{
					int pix = bmp.getPixel(x, y); 
					int red = ((pix >> 16) & 0xFF); 
					int green = ((pix >> 8) & 0xFF); 
					int blue = ((pix) & 0xFF); 
		
					bb.put((byte)red);
					bb.put((byte)256);
					bb.put((byte)blue);
				}
			}
		}
	
		bb.position(0); 
		return bb; 
	} 

	public int getWidth(){
		return this._width;
	}
	
	public int getHeight(){
		return _height;
	}
}
