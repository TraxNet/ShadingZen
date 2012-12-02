package org.traxnet.shadingzen.util.wallpaper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;




public abstract class BaseConfigChooser implements org.traxnet.shadingzen.util.wallpaper.EGLConfigChooser {
        public BaseConfigChooser(int[] configSpec) {
                mConfigSpec = configSpec;
        }
 
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                int[] num_config = new int[1];
 
                egl.eglChooseConfig(display, mConfigSpec, null, 0, num_config);
 
                int numConfigs = num_config[0];
 
                if (numConfigs <= 0) {
                        throw new IllegalArgumentException("No configs match configSpec");
                }
 
                EGLConfig[] configs = new EGLConfig[numConfigs];
                egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs,
                                num_config);
                EGLConfig config = chooseConfig(egl, display, configs);
 
                if (config == null) {
                        throw new IllegalArgumentException("No config chosen");
                }
                return config;
        }
 
        abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                        EGLConfig[] configs);
 
        protected int[] mConfigSpec;
 
        public static class ComponentSizeChooser extends BaseConfigChooser {
                public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
                        super(new int[] { EGL10.EGL_RED_SIZE, redSize,
                                                          EGL10.EGL_GREEN_SIZE, greenSize,
                                                          EGL10.EGL_BLUE_SIZE, blueSize,
                                                          EGL10.EGL_ALPHA_SIZE, alphaSize,
                                                          EGL10.EGL_DEPTH_SIZE, depthSize,
                                                          EGL10.EGL_STENCIL_SIZE, stencilSize,
                                                          EGL10.EGL_RENDERABLE_TYPE, 4,
                                                          EGL10.EGL_NONE });
                        mValue = new int[1];
                        mRedSize = redSize;
                        mGreenSize = greenSize;
                        mBlueSize = blueSize;
                        mAlphaSize = alphaSize;
                        mDepthSize = depthSize;
                        mStencilSize = stencilSize;
                }
 
                @Override
                public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
                        EGLConfig closestConfig = null;
                        int closestDistance = 1000;
 
                        for (EGLConfig config : configs) {
                                int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
                                int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
                               
                                if (d >= mDepthSize && s >= mStencilSize) {
                                        int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                                        int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                                        int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                                        int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                                        int distance = Math.abs(r - mRedSize)
                                                                 + Math.abs(g - mGreenSize)
                                                                 + Math.abs(b - mBlueSize)
                                                                 + Math.abs(a - mAlphaSize);
                                        if (distance < closestDistance) {
                                                closestDistance = distance;
                                                closestConfig = config;
                                        }
                                }
                        }
                        return closestConfig;
                }
 
                private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                EGLConfig config, int attribute, int defaultValue) {
 
                        if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
                                return mValue[0];
                        }
                        return defaultValue;
                }
 
                private int[] mValue;
                // Subclasses can adjust these values:
                protected int mRedSize;
                protected int mGreenSize;
                protected int mBlueSize;
                protected int mAlphaSize;
                protected int mDepthSize;
                protected int mStencilSize;
        }
 
        /**
         * This class will choose a supported surface as close to RGB565 as
         * possible, with or without a depth buffer.
         *
         */
        public static class SimpleEGLConfigChooser extends ComponentSizeChooser {
                public SimpleEGLConfigChooser(boolean withDepthBuffer) {
                        super(4, 4, 4, 0, withDepthBuffer ? 16 : 0, 0);
                        // Adjust target values. This way we'll accept a 4444 or
                        // 555 buffer if there's no 565 buffer available.
                        mRedSize = 5;
                        mGreenSize = 6;
                        mBlueSize = 5;
                }
        }
}