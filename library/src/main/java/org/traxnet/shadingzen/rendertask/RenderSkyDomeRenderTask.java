package org.traxnet.shadingzen.rendertask;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import org.traxnet.shadingzen.core.BitmapTexture;
import org.traxnet.shadingzen.core.CubeShape;
import org.traxnet.shadingzen.core.RenderService;
import org.traxnet.shadingzen.core.ShadersProgram;

/**
 * Used by the SkyDomeScene actor.
 */
public class RenderSkyDomeRenderTask extends RenderTask {
    CubeShape _shape;
    BitmapTexture _texture;
    boolean _needsBufferUpdate = true;
    float [] _modelMatrix;
    float [] _mvp = new float[16];
    float [] _mv = new float[16];

    public RenderSkyDomeRenderTask(){}

    public void initWithCubemapTexture(BitmapTexture cubemap_texture, ShadersProgram program, CubeShape shape, float [] model_matrix) {
        _shape = shape;
        _program = program;
        _texture = cubemap_texture;
        _modelMatrix = model_matrix;


    }

    @Override
    public void onDraw(RenderService service) throws Exception {
        bindDepthTestIfSet();
        checkGlError("RenderSkyDomeRenderTask onDraw 0");
        service.setProgram(_program);
        checkGlError("RenderSkyDomeRenderTask onDraw 1");
        setProgramUniforms(service);

        if(null != _texture)
            _texture.bindTexture(0);
        checkGlError("RenderSkyDomeRenderTask onDraw 2");

        _shape.onDraw(service);

        if(null != _texture)
            _texture.unbindTexture(0);

        _program.unbindProgram();

    }

    @Override
    public boolean onDriverLoad(Context context) {
        if(_needsBufferUpdate){
            _shape.onDriverLoad(context);
            _needsBufferUpdate = false;
        }

        return true;
    }

    void setProgramUniforms(RenderService service) throws Exception {

        Matrix.multiplyMM(_mv, 0, service.getViewMatrix(), 0, _modelMatrix, 0);


        _mv[12] = 0.f;
        _mv[13] = 0.f;
        _mv[14] = 0.f;

        Matrix.multiplyMM(_mvp, 0, service.getProjectionMatrix(), 0, _mv, 0);
        // Move to the shaders as an uniform
        //GLES20.glUniformMatrix4fv(_program.getUniformLocation("p_matrix"), 1, false, service.getProjectionMatrix(), 0);
        GLES20.glUniformMatrix4fv(_program.getUniformLocationNoCheck("mvp_matrix"), 1, false, _mvp, 0);
        checkGlError("RenderSkyDomeRenderTask setProgramUniforms 1");
        GLES20.glUniform1i(_program.getUniformLocationNoCheck("tex_skydome"), 0);
        checkGlError("RenderSkyDomeRenderTask setProgramUniforms 2");
    }

    @Override
    public void initializeFromPool() {
        // TODO Auto-generated method stub

    }

    @Override
    public void finalizeFromPool() {
        // TODO Auto-generated method stub

    }

}
