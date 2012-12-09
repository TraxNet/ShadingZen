package org.traxnet.shadingzen.core2d;

import org.traxnet.shadingzen.math.Vector2;

import java.util.Vector;

/**
 */
public class SpriteAnimationDefinition {
    public Vector<Vector2> frames;

    public SpriteAnimationDefinition(){
        frames = new Vector<Vector2>();
    }

    /**
     * Adds a frame to the animation
     * @param x frame x origin position within the texture
     * @param y frame y origin position within the texture
     */
    public void addFrame(float x, float y){
        frames.add(new Vector2(x, y));
    }
}
