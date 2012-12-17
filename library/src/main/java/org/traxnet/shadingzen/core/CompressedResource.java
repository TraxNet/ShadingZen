package org.traxnet.shadingzen.core;

import android.content.Context;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
public abstract class CompressedResource extends Resource {
    public abstract boolean onCompressedStorageLoad(Context context, String id, ZipFile zipfile, ZipEntry resource_entry, Object params);
}
