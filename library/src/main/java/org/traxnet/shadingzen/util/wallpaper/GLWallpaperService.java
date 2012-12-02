package org.traxnet.shadingzen.util.wallpaper;

import android.content.Context;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

 
public class GLWallpaperService extends WallpaperService {
        private static final String TAG = "GLWallpaperService";
        private org.traxnet.shadingzen.core.Engine _engine = null;
        @Override
        public Engine onCreateEngine() {
                return new GLEngine();
        }
 
        public class GLEngine extends Engine {
                public final static int RENDERMODE_WHEN_DIRTY = 0;
                public final static int RENDERMODE_CONTINUOUSLY = 1;
 
                private GLThread mGLThread;
                private EGLConfigChooser mEGLConfigChooser;
                private EGLContextFactory mEGLContextFactory;
                private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
                private GLWrapper mGLWrapper;
                private int mDebugFlags;
                private org.traxnet.shadingzen.core.Renderer _renderer;
                private Context _context;
 
                public GLEngine() {
                        super();
                }
 
                @Override
                public void onVisibilityChanged(boolean visible) {
                        if (visible) {
                                onResume();
                        } else {
                                onPause();
                        }
                        super.onVisibilityChanged(visible);
                }
 
                @Override
                public void onCreate(SurfaceHolder surfaceHolder) {
                        super.onCreate(surfaceHolder);
                        // Log.d(TAG, "GLEngine.onCreate()");
                        
                        _engine = new org.traxnet.shadingzen.core.Engine(surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
                        _engine.Init(_context, _renderer);
                }
 
                @Override
                public void onDestroy() {
                        super.onDestroy();
                        // Log.d(TAG, "GLEngine.onDestroy()");
                        mGLThread.requestExitAndWait();
                }
 
                @Override
                public void onSurfaceChanged(SurfaceHolder holder, int format,
                                int width, int height) {
                        // Log.d(TAG, "onSurfaceChanged()");
                        mGLThread.onWindowResize(width, height);
                        super.onSurfaceChanged(holder, format, width, height);
                }
 
                @Override
                public void onSurfaceCreated(SurfaceHolder holder) {
                        Log.d(TAG, "onSurfaceCreated()");
                        mGLThread.surfaceCreated(holder);
                        super.onSurfaceCreated(holder);
                }
 
                @Override
                public void onSurfaceDestroyed(SurfaceHolder holder) {
                        Log.d(TAG, "onSurfaceDestroyed()");
                        mGLThread.surfaceDestroyed();
                        super.onSurfaceDestroyed(holder);
                }
 
                /**
                 * An EGL helper class.
                 */
                public void setGLWrapper(GLWrapper glWrapper) {
                        mGLWrapper = glWrapper;
                }
 
                public void setDebugFlags(int debugFlags) {
                        mDebugFlags = debugFlags;
                }
 
                public int getDebugFlags() {
                        return mDebugFlags;
                }
 
                public void setRenderer(org.traxnet.shadingzen.core.Renderer renderer, Context context) {
                        checkRenderThreadState();
                        if (mEGLConfigChooser == null) {
                                mEGLConfigChooser = new BaseConfigChooser.SimpleEGLConfigChooser(
                                                true);
                        }
                        if (mEGLContextFactory == null) {
                                mEGLContextFactory = new DefaultContextFactory();
                        }
                        if (mEGLWindowSurfaceFactory == null) {
                                mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
                        }
                        mGLThread = new GLThread(renderer, mEGLConfigChooser,
                                        mEGLContextFactory, mEGLWindowSurfaceFactory, mGLWrapper);
 
                        _renderer =renderer;
                        _context = context;
                        mGLThread.start();
                }
 
                public void setEGLContextFactory(EGLContextFactory factory) {
                        checkRenderThreadState();
                        mEGLContextFactory = factory;
                }
 
                public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
                        checkRenderThreadState();
                        mEGLWindowSurfaceFactory = factory;
                }
 
                public void setEGLConfigChooser(EGLConfigChooser configChooser) {
                        checkRenderThreadState();
                        mEGLConfigChooser = configChooser;
                }
 
                public void setEGLConfigChooser(boolean needDepth) {
                        setEGLConfigChooser(new BaseConfigChooser.SimpleEGLConfigChooser(
                                        needDepth));
                }
 
                public void setEGLConfigChooser(int redSize, int greenSize,
                                int blueSize, int alphaSize, int depthSize, int stencilSize) {
                        setEGLConfigChooser(new BaseConfigChooser.ComponentSizeChooser(
                                        redSize, greenSize, blueSize, alphaSize, depthSize,
                                        stencilSize));
                }
 
                public void setRenderMode(int renderMode) {
                        mGLThread.setRenderMode(renderMode);
                }
 
                public int getRenderMode() {
                        return mGLThread.getRenderMode();
                }
 
                public void requestRender() {
                        mGLThread.requestRender();
                }
 
                public void onPause() {
                        mGLThread.onPause();
                }
 
                public void onResume() {
                        mGLThread.onResume();
                }
 
                public void queueEvent(Runnable r) {
                        mGLThread.queueEvent(r);
                }
 
                private void checkRenderThreadState() {
                        if (mGLThread != null) {
                                throw new IllegalStateException(
                                                "setRenderer has already been called for this instance.");
                        }
                }
        }
}