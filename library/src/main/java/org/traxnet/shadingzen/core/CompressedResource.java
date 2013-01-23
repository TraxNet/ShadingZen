package org.traxnet.shadingzen.core;

import android.content.Context;

import java.util.zip.ZipFile;

/**
 */
public abstract class CompressedResource extends Resource {
    public abstract boolean onCompressedStorageLoad(Context context, ResourcesManager manager, String id, ZipFile zipfile, String location, Object params);
}
