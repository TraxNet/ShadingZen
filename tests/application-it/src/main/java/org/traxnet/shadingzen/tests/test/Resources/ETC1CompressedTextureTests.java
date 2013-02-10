package org.traxnet.shadingzen.tests.test.Resources;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.tests.DummyTestActivity;

/**
 * Copyright (c) Oscar Blasco Maestro, 2013.
 * Date: 8/02/13
 * Time: 6:22
 */
public class ETC1CompressedTextureTests extends ActivityInstrumentationTestCase2<DummyTestActivity> {

    public ETC1CompressedTextureTests() {
        super(DummyTestActivity.class);
    }

   /* protected void setUp() throws Exception {
        super.setUp();
        //getActivity().waitForRenderer();

        //ResourcesManager.getSharedInstance().setExpansionPack("/mnt/sdcard/shadingzen/org.traxnet.shadingzen.tests.resources.zip");

       // Renderer render = (Renderer) Engine.getSharedInstance().getRenderService();

        try{
            while(!getActivity().rendererIsReady())
                Thread.sleep(100);
        } catch (InterruptedException ex){

        }
    }

    public void testCanCreateETC1Texture(){
        TextureParameters params = new TextureParameters(64, 64);
        Texture texture = (Texture) ResourcesManager.getSharedInstance().factoryCompressed(CompressedTexture.class, null, "text01", "textures/tex01", params);
        assertNotNull(texture);
    }    */
}
