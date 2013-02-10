package org.traxnet.shadingzen.core.resources;

import android.opengl.GLES20;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 8/02/13
 * Time: 7:24
 */
public class TextureParameters {




    public enum TextureType{
        Texture2D,
        TextureCubeMap
    }
    public enum TextureFilter{
        Nearest,
        Linear,
        Bilinear
    }

    public enum TextureFormat {
        RGBA8,
        RGB565
    }

    TextureType type;
    TextureFilter magFilter, minFilter;
    TextureFormat internalFormat;
    boolean genMipmaps;
    public boolean resizeTexturesToPowerOfTwo = true;
    int textureWidth;
    int textureHeight;

    public TextureParameters(int width, int height){
        textureWidth = width;
        textureHeight = height;
        type = TextureType.Texture2D;
        magFilter = TextureFilter.Linear;
        minFilter = TextureFilter.Linear;
        internalFormat = TextureFormat.RGBA8;
        genMipmaps = true;

    }

    public TextureParameters(int width, int height, TextureType texttype, TextureFormat internal_format, TextureFilter mag, TextureFilter min)
    {
        textureWidth = width;
        textureHeight = height;
        type = texttype;
        internalFormat = internal_format;
        minFilter = min;
        magFilter = mag;
    }

    public void setType(TextureType type){
        this.type = type;
    }

    public TextureType getType(){
        return type;
    }


    public TextureFilter getMinFilter(){
        return minFilter;
    }
    public void setMinFilter(TextureFilter filter){
        minFilter = filter;
    }
    public TextureFilter getMagFilter(){
        return magFilter;
    }
    public void setMagFilter(TextureFilter filter){
        magFilter = filter;
    }

    public int getInternalFormatOGL() {
        switch (internalFormat){
            case RGBA8:
                return GLES20.GL_RGBA;
            case RGB565:
                return GLES20.GL_RGB565;
            default:
                return GLES20.GL_RGBA;
        }
    }

    public int getMagFilterOGL(){
        return mipmapFilterToOpenGLESFilter(magFilter);
    }

    public int getMinFilterOGL(){
        return mipmapFilterToOpenGLESFilter(minFilter);
    }

    private int mipmapFilterToOpenGLESFilter(TextureFilter filter) {
        switch (filter){
            case Nearest:
                return GLES20.GL_NEAREST;

            case Bilinear:
                return GLES20.GL_LINEAR_MIPMAP_LINEAR;
            default:
            case Linear:
                return GLES20.GL_LINEAR;
        }
    }

    public int getWidth() {
        return textureWidth;
    }

    public int getHeight() {
        return textureHeight;
    }

    public void setGenMipMaps(boolean mode)
    {
        genMipmaps = mode;
    }
    public boolean getGenMipMaps(){
        return genMipmaps;
    }
}
