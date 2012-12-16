package org.traxnet.shadingzen.core.actors;

import android.util.Log;
import org.traxnet.shadingzen.R;
import org.traxnet.shadingzen.core.*;
import org.traxnet.shadingzen.core.BitmapTexture.TextureType;
import org.traxnet.shadingzen.math.Matrix4;
import org.traxnet.shadingzen.rendertask.RenderTaskPool;
import org.traxnet.shadingzen.rendertask.RenderSkyDomeRenderTask;

public class SkyDomeScene extends Scene {
    CubeShape _cubeShape;
    BitmapTexture _texture;

    public void init(float dome_size, int cubemap_res0, int cubemap_res1, int cubemap_res2,
                     int cubemap_res3, int cubemap_res4, int cubemap_res5) throws Exception {
        _program = initSkyDomeProgram();

        _cubeShape = (CubeShape) resourceFactory(CubeShape.class, "CubeShape", 0);

        try{
            _cubeShape.initWithRaidus(dome_size, _program);

            _texture = loadCubeMap(cubemap_res0, cubemap_res1, cubemap_res2,
                    cubemap_res3, cubemap_res4, cubemap_res5);


        } catch (Exception ex){
            Log.e("ShadingZen", "Failed to load SkyDomeScene Actor", ex);
        }
    }

    BitmapTexture loadCubeMap(int cubemap_res0, int cubemap_res1, int cubemap_res2,
                              int cubemap_res3, int cubemap_res4, int cubemap_res5) throws Exception {
        BitmapTexture.Parameters params = new BitmapTexture.Parameters();
        params.setType(TextureType.TextureCubeMap);
        params.setCubeMapImage(0, cubemap_res0);
        params.setCubeMapImage(1, cubemap_res1);
        params.setCubeMapImage(2, cubemap_res2);
        params.setCubeMapImage(3, cubemap_res3);
        params.setCubeMapImage(4, cubemap_res4);
        params.setCubeMapImage(5, cubemap_res5);

        BitmapTexture texture = (BitmapTexture) ResourcesManager.getSharedInstance().factory(BitmapTexture.class, (Entity)this, "SkyDomeTexture", R.raw.alpha_tex, params);
        return texture;
    }

    ShadersProgram initSkyDomeProgram(){
        ShadersProgram program = (ShadersProgram) resourceFactory(ShadersProgram.class, "SkyDomeProgram", 0);

        if(!program.isProgramDefined()){
            program.setName("SkyDomeProgram");
            program.attachVertexShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_skydome_vertex));
            program.attachFragmentShader(ResourcesManager.getSharedInstance().loadResourceString(R.raw.shader_skydome_fragment));
            program.setProgramsDefined();
        }

        return program;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDraw(RenderService renderer){
        RenderSkyDomeRenderTask task = (RenderSkyDomeRenderTask) RenderTaskPool.sharedInstance().newTask(RenderSkyDomeRenderTask.class);

        task.initWithCubemapTexture(_texture, _program, _cubeShape, Matrix4.identity().getAsArray());
        renderer.addRenderTask(task);
    }

    @Override
    public void onLoad() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUnload() {
        // TODO Auto-generated method stub

    }


    public class SkyDomeTextureList {
        public int cubeMapTexture0 = -1;
        public int cubeMapTexture1 = -1;
        public int cubeMapTexture2 = -1;
        public int cubeMapTexture3 = -1;
        public int cubeMapTexture4 = -1;
        public int cubeMapTexture5 = -1;
        public int cubeMapTexture6 = -1;
    }
}
