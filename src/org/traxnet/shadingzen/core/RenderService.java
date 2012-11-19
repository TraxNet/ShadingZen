package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.math.Vector3;
import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.rendertask.RenderTask;

import android.content.Context;

public interface RenderService {
	public void setProgram(ShadersProgram program);
	public float[] getViewMatrix();
	public float[] getProjectionMatrix();
	public float[] getOrthoProjectionMatrix();
	public Vector3 getCameraPosition();
	public void addRenderTask(RenderTask task);
	public Context getContext();
	public void checkGlError(String op);
	public void setClearColor(Vector4 color);
}
