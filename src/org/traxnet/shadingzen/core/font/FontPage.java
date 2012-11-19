package org.traxnet.shadingzen.core.font;

import java.util.StringTokenizer;

import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.Entity;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ResourcesManager;
import org.traxnet.shadingzen.core.Texture;

import android.content.Context;

public class FontPage extends Entity{
	short _id = 0;
	String _file = null;
	Texture _texture;
	
	public void parseTokens(StringTokenizer tokenizer, Context context, int texture_res){
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			StringTokenizer valued_token = new StringTokenizer(token, "=");
			String type = valued_token.nextToken();
			if(type.equalsIgnoreCase("id")){
				_id = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("file")){
				_file = valued_token.nextToken();
			} 
		}
		
		//String id = _file.substring(0, _file.lastIndexOf('.'));
		//int res_id = context.getResources().getIdentifier(id, "raw", packageid);

		Texture.Parameters params = new Texture.Parameters();
		_texture = (Texture) ResourcesManager.getSharedInstance().factory(Texture.class, (Entity)this, _file, texture_res, params);
		
	}
	
	public Texture getTexture(){
		return _texture;
	}
	
	public void bindPage(int unit){
		_texture.bindTexture(unit);
	}
	
	public void unbindPage(int unit){
		_texture.unbindTexture(unit);
	}

	@Override
	public void onTick(float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDraw(RenderService renderer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload() {
		// TODO Auto-generated method stub
		
	}
}
