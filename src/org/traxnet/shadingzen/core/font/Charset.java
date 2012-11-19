package org.traxnet.shadingzen.core.font;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Charset {
	short _lineHeight;
	short _base;
	short _width, _height;
	short _pages;
	CharDescriptor [] _chars;
	
	public Charset(int capacity){
		_chars = new CharDescriptor[capacity];	
	}
	
	public void addChar(int index, CharDescriptor char_desc){
		//_chars.add(index, char_desc);
		_chars[index] = char_desc;
	}
	
	public short getLineHeight(){
		return _lineHeight;
	}
	
	public short getBase(){
		return _base;
	}
	
	public short getWidth(){
		return _width;
	}
	
	public short getHeight(){
		return _height;
	}
	
	public short getPages(){
		return _pages;
	}
	
	public CharDescriptor getChar(short id){
		//return _chars.get(id);
		return _chars[id];
	}
	
	public void parseTokens(StringTokenizer tokenizer){
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			StringTokenizer valued_token = new StringTokenizer(token, "=");
			String type = valued_token.nextToken();
			if(type.equalsIgnoreCase("lineHeight")){
				_lineHeight = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("base")){
				_base = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("scaleW")){
				_width = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("scaleH")){
				_height = Short.parseShort(valued_token.nextToken());
			} else if(type.equalsIgnoreCase("pages")){
				_pages = Short.parseShort(valued_token.nextToken());
			} 
		}
	}
}
