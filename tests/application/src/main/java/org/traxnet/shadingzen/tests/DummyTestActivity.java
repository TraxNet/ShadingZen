package org.traxnet.shadingzen.tests;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class DummyTestActivity extends Activity /*implements RenderNotificationsDelegate*/ {

    private static String TAG = "shadingzen-tests";

   // EngineGLSurfaceView surfaceView;
    //Renderer renderer;
    private boolean  _isRendererReady;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.main);

       /* renderer = new Renderer(this);
        surfaceView = new EngineGLSurfaceView(this, renderer);
        LinearLayout layout = (LinearLayout) findViewById(R.id.fullscreen_content);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        layout.addView(surfaceView, lparams);   */

        //layout.invalidate();
    }

    public void waitForRenderer(){
        //surfaceView.waitForRenderer();
    }

    public boolean rendererIsReady(){
        return _isRendererReady;
    }

    public void onRenderCreated() {
        /*try{
            ResourcesManager.getSharedInstance().setExpansionPack("/mnt/sdcard/shadingzen/org.traxnet.shadingzen.tests.resources.zip");
        } catch (Exception ex){

        }       */
        _isRendererReady = true;
    }
}

