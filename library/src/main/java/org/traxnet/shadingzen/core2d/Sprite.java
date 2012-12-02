package org.traxnet.shadingzen.core2d;

import java.util.UUID;

import org.traxnet.shadingzen.core.Entity;
import org.traxnet.shadingzen.core.ResourcesManager;
import org.traxnet.shadingzen.core.BitmapTexture;

public class Sprite extends QuadAtlas {
	public Sprite(){
		
	}
	
	public static Sprite spriteWithTexture(Node2d parent_node, BitmapTexture texture, String actor_name, short x, short y){
		Sprite node = (Sprite) parent_node.spawn(Sprite.class, actor_name);
		node.init(1, texture);
		
		node.AddQuad(new Quad(0, 0, 0, texture.getWidth(), texture.getHeight(), 0.f, 1.f, 1.f, 0.f));
		node.setContentSize(texture.getWidth(), texture.getHeight());
		
		return node;
	}
	
	
	public static Sprite spriteWithTexture(Node2d parent_node, int res_id, short x, short y){
		Sprite node = (Sprite) parent_node.spawn(Sprite.class, UUID.randomUUID().toString());
		
		BitmapTexture texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)node, ""+res_id, res_id, new BitmapTexture.Parameters());
		
		
		node.init(1, texture);
		
		node.AddQuad(new Quad(0, 0, 0, texture.getWidth(), texture.getHeight(), 0.f, 1.f, 1.f, 0.f));
		node.setContentSize(texture.getWidth(), texture.getHeight());
		
		return node;
	}
}
