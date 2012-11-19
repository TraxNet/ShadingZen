package org.traxnet.shadingzen.core.font;

import java.util.StringTokenizer;

public class CharDescriptor {
	short _id;
	short _x, _y;
	short _width, _height;
	short _xOffset, _yOffset;
	short _xAdvance;
	short _page;
	
	public short getPage(){
		return _page;
	}
	
	public short getXAdvance(){
		return _xAdvance;
	}
	
	public short getXOffset(){
		return _xOffset;
	}
	
	public short getYOffset(){
		return _yOffset;
	}
	
	public short getWidth(){
		return _width;
	}
	
	public short getHeight(){
		return _height;
	}
	
	public short getX(){
		return _x;
	}
	
	public short getY(){
		return _y;
	}
	
	public short getId(){
		return _id;
	}
	
	
	void parseTokens(StringTokenizer tokenizer){
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			StringTokenizer valued_token = new StringTokenizer(token, "=");
			String type = valued_token.nextToken();
			if(type.equalsIgnoreCase("id")){
				_id = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("x")){
				_x = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("y")){
				_y = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("width")){
				_width = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("height")){
				_height = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("xoffset")){
				_xOffset = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("yoffset")){
				_yOffset = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("xadvance")){
				_xAdvance = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("page")){
				_page = Short.parseShort(valued_token.nextToken());
			}
		}
	}
}
