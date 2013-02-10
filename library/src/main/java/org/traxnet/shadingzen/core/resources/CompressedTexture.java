package org.traxnet.shadingzen.core.resources;

import android.content.Context;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.util.Log;
import org.traxnet.shadingzen.core.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 8/02/13
 * Time: 6:19
 */
public class CompressedTexture extends CompressedResource implements Texture {
    CompressionBuffer [] dataBuffers;
    protected IntBuffer _textureIds;
    protected boolean _driverDataDirty = true;
    protected int numMipMapLevels = 0;
    TextureParameters textureParams;

    class CompressionBuffer{
        public ByteBuffer compressedBuffer;
        public int width, height;
        public InputStream inputStream;
    }

    @Override
    public boolean onCompressedStorageLoad(Context context, ResourcesManager manager, String id, ZipFile zipfile, String location, Object params) {

        try{
            String zipfile_location = String.format("resources/%s", location);
            ZipEntry entry = zipfile.getEntry(zipfile_location);

            if(null == entry){
                Log.e("ShadingZen", "Given compressed resource location was not found:" + zipfile_location);
                return false;
            }

            //if(entry.isDirectory()){
                RenderService renderer = Engine.getSharedInstance().getRenderService();
                if(renderer.isDeviceCapabilitySupported(RenderDeviceCapability.TEXTURE_COMPRESSION_ETC1)){
                    return loadAsETC1Texture(manager, id, zipfile, location, params);
                }

                //return false;
            //} else {
                //Log.e("ShadingZen", "Currently loading directly compressed textures by filename is not supported");
            //}
        } catch(Exception ex){
            Log.e("ShadingZen", "Error getting input stream from zip file:" + ex.getMessage(), ex);
        }

        return false;
    }

    private boolean loadAsETC1Texture(ResourcesManager manager, String id, ZipFile zipfile, String location, Object params) {
        textureParams = (TextureParameters) params;
        if(null == textureParams){
            Log.e("ShadingZen", "Cannot load compressed textures without a TextureParameter. Calll factory with a TextureParameter");
            return false;
        }

        int num_miplevels = 10 - manager.getDefaultMimapLevel();
        int width = textureParams.getWidth();
        int height = textureParams.getHeight();

        dataBuffers = new CompressionBuffer[num_miplevels];

        String [] strings = location.split("/");

        for(int level = manager.getDefaultMimapLevel(); level < num_miplevels; level++){
            width /= (int) Math.pow(2, level);
            height /= (int) Math.pow(2, level);

            if(width <= 16 || height <= 16)
                break;

            String filepath = String.format("resources/%s/%s_mip_%d.pkm", location, strings[1], level);
            ZipEntry entry = zipfile.getEntry(filepath);

            if(null == entry){
                Log.i("ShadingZen", String.format("Compressed miplevel %d not found for texture location %s", level, location));
                continue;
            }

            try{
                InputStream input_stream = zipfile.getInputStream(entry);

                dataBuffers[level] = new CompressionBuffer();
                dataBuffers[level].width = width;
                dataBuffers[level].height = height;
                dataBuffers[level].inputStream = input_stream;
                //dataBuffers[level].compressedBuffer = readToByteBuffer(input_stream, 1024);

            } catch (IOException ex){
                Log.e("ShadingZen", String.format("loadAsETC1Texture: Error loading mipmap level %i from location %s: %s", level, filepath, ex.getMessage()));
                continue;
            }

            numMipMapLevels++;
        }

        if(0 == numMipMapLevels)
            return false;

        return true;
    }

    static ByteBuffer readToByteBuffer(InputStream inStream, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
        int read;
        while (true) {
            read = inStream.read(buffer);
            if (read == -1)
                break;
            outStream.write(buffer, 0, read);
        }
        ByteBuffer byteData = ByteBuffer.wrap(outStream.toByteArray());
        return byteData;
    }

    @Override
    public boolean onStorageLoad(Context context, String id, int resource_id, Object params) {
        return false;
    }

    @Override
    public boolean onDriverLoad(Context context) {
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);

        _textureIds = IntBuffer.allocate(1);
        GLES20.glGenTextures(1, _textureIds);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureIds.get(0));

        try{
            for(int level = 0; level < 1; level++){
                CompressionBuffer data_buffer = dataBuffers[level];
                //ByteBuffer buffer = data_buffer.compressedBuffer;

                ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, level, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, dataBuffers[level].inputStream);
                //GLES20.glCompressedTexImage2D(GLES20.GL_TEXTURE_2D, level, textureParams.getInternalFormatOGL(), data_buffer.width, data_buffer.height, 0, buffer.capacity(), buffer);
            }
        } catch (IOException ex){
            Log.e("ShadingZen", "Error loading compressed ETC1 texture: " + ex.getMessage());
            _driverDataDirty = false;
            return false;
        }


        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, textureParams.getMinFilterOGL());
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, textureParams.getMagFilterOGL());

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
    public void onRelease() {
        GLES20.glDeleteTextures(1, _textureIds);

        for(int index = 0; index < dataBuffers.length; index++){
            dataBuffers[index].compressedBuffer.clear();
        }

        dataBuffers = null;
        _textureIds = null;
    }


    @Override
    public boolean isDriverDataDirty() {
        return _driverDataDirty;
    }

    @Override
    public void bindTexture(int unit) {

    }

    @Override
    public void unbindTexture(int unit) {

    }

    @Override
    public int getWidth() {
        return textureParams.getWidth();
    }

    @Override
    public int getHeight() {
        return textureParams.getHeight();
    }
}
