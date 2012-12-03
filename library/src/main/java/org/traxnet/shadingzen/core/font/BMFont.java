package org.traxnet.shadingzen.core.font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class BMFont {
	Charset _charset;
	String _face;
	short _size = 0;
	boolean _bold = false;
	boolean _italic = false;
	boolean _unicode = false;
	boolean _smooth = false;
	boolean _aa = false;
	short _paddingLeft = 0, _paddingRight = 0, _paddingTop = 0, _paddingDown = 0;
	short _spacingX = 0, _spacingY = 0;
	Vector<FontPage> _pages;
	
	public BMFont(){
		_pages = new Vector<FontPage>();
	}
	
	public boolean loadFromResource(Context context, int resource_id, int texture_res){
		InputStream input_stream = context.getResources().openRawResource(resource_id);

		BufferedReader reader = new BufferedReader(new InputStreamReader(input_stream));
		
		try{
			return loadBuffer(reader, context, /*context.getResources().getResourcePackageName(resource_id)*/ texture_res);
		} catch(IOException e){
			Log.e("ShadingZen", "Error loading font for resource_id:" + resource_id, e);
			
			return false;
		}
	}
	
	public CharDescriptor getChar(short ascii_val){
		return _charset.getChar(ascii_val);
	}
	
	public Charset getCharset(){
		return _charset;
	}
	
	public FontPage getPage(int id){
		return _pages.get(id);
	}
	
	boolean loadBuffer(BufferedReader in, Context context, int texture_res) throws IOException
	{
		String line = in.readLine();
		while(null != line){
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			String token = tokenizer.nextToken();
			if(token.equalsIgnoreCase("common")){
				_charset = new Charset(256);
				_charset.parseTokens(tokenizer);
			} else if(token.equalsIgnoreCase("char")){
				CharDescriptor chardesc = new CharDescriptor();
				chardesc.parseTokens(tokenizer);
				_charset.addChar(chardesc.getId(), chardesc);
			} else if(token.equalsIgnoreCase("info")){
				loadInfo(tokenizer);
			} else if(token.equalsIgnoreCase("page")){
				FontPage page = new FontPage();
				page.parseTokens(tokenizer, context, texture_res);
				_pages.add(page);
			} 
			
			line = in.readLine();
		}
		
		return true;
	}
	
	void loadInfo(StringTokenizer tokenizer){
		
	}
}
