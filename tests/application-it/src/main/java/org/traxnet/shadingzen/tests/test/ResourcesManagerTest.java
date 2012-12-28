package org.traxnet.shadingzen.tests.test;

import android.test.ActivityInstrumentationTestCase2;
import org.traxnet.shadingzen.tests.*;
import org.traxnet.shadingzen.core.*;

public class ResourcesManagerTest extends ActivityInstrumentationTestCase2<DummyTestActivity> {

    public ResourcesManagerTest() {
        super(DummyTestActivity.class);
    }

    public void testManagerInit() {
        DummyTestActivity activity = getActivity();
        assertNotNull(activity);

        ResourcesManager res_manager = new ResourcesManager();
        assertNotNull(res_manager);

    }

    public void testManagerSetExpansionPack() {
        ResourcesManager res_manager = createResourcesManager();
    }

    private ResourcesManager createResourcesManager() {
        ResourcesManager res_manager = new ResourcesManager();
        assertNotNull(res_manager);

        try{
            res_manager.setExpansionPack("/mnt/sdcard/shadingzen/org.traxnet.shadingzen.tests.resources.zip");
            res_manager.setContext(getActivity().getApplicationContext());
        } catch (Exception ex){
            assertTrue("Exception trown:" + ex.getMessage(), false);
        }
        return res_manager;
    }

    public void testBitmap2DDecodingFromExmpansionPack(){
        ResourcesManager res_manager = createResourcesManager();

        BitmapTexture.Parameters params = new BitmapTexture.Parameters();
        CompressedResource res = res_manager.factoryCompressed(BitmapTexture.class,  null, "test", "", params);
        assertNull(res);

        res = res_manager.factoryCompressed(BitmapTexture.class,  null, "test", "resources/textures/exodus_01.jpg", params);
        assertNotNull(res);
    }


}

