package org.traxnet.shadingzen.core;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.opengl.GLES20;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.Shape;

@Deprecated // This class doesn't work
public class Sphere extends Shape {

	float sphere_parms[]=new float[3];
	
	boolean _isDriverDataDirty;

	double mRaduis;
	double mStep;
	float mVertices[];
	int mPoints;
	
	private IntBuffer _bufferIds;

	
	 private final int slices = 25;
     private final int stacks = 25;
     private final float radius = 1.f;

     private float[] triangleFanVertices;// = new float[(slices + 2)*3];
     private float[] triangleFanNormals;// = new float[(slices + 2)*3];

     private float[] triangleStripVertices;
     private float[] triangleStripNormals;

     private FloatBuffer fanVertices;
     private FloatBuffer fanNormals;
     private FloatBuffer stripVertices;
     private FloatBuffer stripNormals;

     public Sphere() {
         
     }

     /**
      * Calculates the sphere's Fans
      */
     void calculateSphereTriangleFan() {
         int fanVertexCount = (slices + 2) * 3;
         triangleFanVertices = new float[fanVertexCount];
         triangleFanNormals = new float[fanVertexCount];

         double drho = Math.PI / stacks;
         double dtheta = 2.0 * Math.PI / slices;

         triangleFanVertices[0] = 0.0f;
         triangleFanVertices[1] = 0.0f;
         triangleFanVertices[2] = radius;
         int counter = 3;
         for (int j = 0; j <= slices; j++) {
             double theta = (j == slices) ? 0.0 : j * dtheta;
             Double xD = new Double((-1.0) * Math.sin(theta)
                     * Math.sin(drho));
             Double yD = new Double(Math.cos(theta) * Math.sin(drho));
             Double zD = new Double(Math.cos(drho));
             triangleFanVertices[counter++] = xD.floatValue() * radius;
             triangleFanVertices[counter++] = yD.floatValue() * radius;
             triangleFanVertices[counter++] = zD.floatValue() * radius;
         }

         triangleFanNormals = normalize(triangleFanVertices);

     }

     /**
      *  Calculates the triangles for the sphere
      */
     void calculateSphereTriangleStrips() {
         int stripVertexCount = ((slices + 1) * 2 * stacks) * 3;
         triangleStripVertices = new float[stripVertexCount];
         triangleStripNormals = new float[stripVertexCount];

         double drho = Math.PI / stacks;
         double dtheta = 2.0 * Math.PI / slices;

         int counter = 0;
         for (int i = 0; i < stacks; i++) {
             double rho = i * drho;
             for (int j = 0; j <= slices; j++) {
                 double theta = (j == slices) ? 0.0 : j * dtheta;
                 Double xD = new Double((-1.0) * Math.sin(theta)
                         * Math.sin(rho));
                 Double yD = new Double(Math.cos(theta) * Math.sin(rho));
                 Double zD = new Double(Math.cos(rho));
                 triangleStripVertices[counter++] = xD.floatValue()
                         * radius;
                 triangleStripVertices[counter++] = yD.floatValue()
                         * radius;
                 triangleStripVertices[counter++] = zD.floatValue()
                         * radius;

                 xD = new Double((-1.0) * Math.sin(theta)
                         * Math.sin(rho + drho));
                 yD = new Double(Math.cos(theta) * Math.sin(rho + drho));
                 zD = new Double(Math.cos(rho + drho));
                 triangleStripVertices[counter++] = xD.floatValue()
                         * radius;
                 triangleStripVertices[counter++] = yD.floatValue()
                         * radius;
                 triangleStripVertices[counter++] = zD.floatValue()
                         * radius;
             }
         }

         triangleStripNormals = normalize(triangleStripVertices);

     }

     float[] normalize(float[] vertices) {
         int vertexCount = vertices.length;
         float[] normals = new float[vertexCount];

         for (int i = 0; i < vertexCount; i += 3) {
             double x2 = new Float(vertices[i] * vertices[i])
                     .doubleValue();
             double y2 = new Float(vertices[i + 1] * vertices[i + 1])
                     .doubleValue();
             double z2 = new Float(vertices[i + 2] * vertices[i + 2])
                     .doubleValue();
             double mag = Math.sqrt(x2 + y2 + z2);

             float xN = new Double(x2 / mag).floatValue();
             float yN = new Double(y2 / mag).floatValue();
             float zN = new Double(z2 / mag).floatValue();

             normals[i] = xN;
             normals[i + 1] = yN;
             normals[i + 2] = zN;

         }

         return normals;
     }
	

	@Override
	public void onDraw(RenderService service) {
		if(_isDriverDataDirty)
			return;
		
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
		
		GLES20.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, triangleFanVertices.length);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, triangleStripVertices.length);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(0);
	}


	@Override
	public boolean onStorageLoad(Context context, String id, int resource_id, Object params) {
		calculateSphereTriangleFan();
        calculateSphereTriangleStrips();

        fanVertices = FloatBuffer.wrap(triangleFanVertices);
        fanNormals = FloatBuffer.wrap(triangleFanNormals);
        stripVertices = FloatBuffer.wrap(triangleStripVertices);
        stripNormals = FloatBuffer.wrap(triangleStripNormals);
	    
	    return true;
	}


	@Override
	public boolean onDriverLoad(Context context) {	
		 _bufferIds = ByteBuffer.allocateDirect(2 * Float.SIZE/8).asIntBuffer();
		GLES20.glGenBuffers(2, _bufferIds);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(0));
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fanVertices.capacity()*4, fanVertices, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _bufferIds.get(1));
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, stripVertices.capacity()*4, stripVertices, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		_isDriverDataDirty = false;
			
		return true;
	}


	@Override
	public boolean onResumed(Context context) {
		// TODO Auto-generated method stub
		_isDriverDataDirty = false;
		return false;
	}


	@Override
	public boolean onPaused(Context context) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isDriverDataDirty() {
		// TODO Auto-generated method stub
		return _isDriverDataDirty;
	}


	@Override
	public void onRelease() {
		// TODO Auto-generated method stub
		
	}

}
