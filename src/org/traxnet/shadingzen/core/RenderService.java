package org.traxnet.shadingzen.core;

import org.traxnet.shadingzen.math.Vector4;
import org.traxnet.shadingzen.rendertask.RenderTask;
import org.traxnet.shadingzen.rendertask.RenderTaskBatch;

import android.content.Context;

public interface RenderService {
	public void setProgram(ShadersProgram program);
	public float[] getViewMatrix();
	public float[] getProjectionMatrix();
	public float[] getOrthoProjectionMatrix();
	public void computeMVPMatricesForCurrentCamera();
	public Camera getCamera();
	public void setCamera(Camera camera);
	public void addRenderTask(RenderTask task);
	public Context getContext();
	public void checkGlError(String op);
	public void setClearColor(Vector4 color);
	public RenderTask getBackgroundRenderTask();
	public void pushRenderBatch(RenderTaskBatch batch);
}
