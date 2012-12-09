package org.traxnet.shadingzen.core2d;

import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.math.Vector2;

import java.util.UUID;
import java.util.Vector;

/**
 * AnimatedSprite exploits a quadatlas concept as a texture sheet where an animation is stored as frames.
 *
 * Use AnimatedSprite and SpriteAnimationAction to create animated sprites.
 */
public class AnimatedSprite extends QuadAtlas {
    private Quad _quad;
    private int _spriteWidth, _spriteHeight;

    public static AnimatedSprite animatedSpriteWithTexture(Node2d parent_node, int res_id, int width, int height){
        AnimatedSprite node = (AnimatedSprite) parent_node.spawn(AnimatedSprite.class, UUID.randomUUID().toString());

        node._texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)node, ""+res_id, res_id, new BitmapTexture.Parameters());

        node._spriteWidth = width;
        node._spriteHeight = height;

        node.init(1, node._texture);

        node._quad = new Quad(0, 0, 0, width, height, 0.f, (float)height/(float)node._texture.getHeight(), (float)width/(float)node._texture.getWidth(), 0.f);
        node.AddQuad(node._quad);
        node.setContentSize(width, height);

        return node;
    }

    /**
     * Updates the quad wv texture coordiantes
     * @param x
     * @param y
     */
    public void setCurrentFrame(float x, float y){
        _quad._u1 = x;
        _quad._v1 = y + (float)_spriteHeight/(float)_texture.getHeight();
        _quad._u2 = x +(float) _spriteWidth/(float)_texture.getWidth();
        _quad._v2 = y;


    }





}
