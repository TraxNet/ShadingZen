package org.traxnet.shadingzen.util.wallpaper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * An interface for customizing the eglCreateContext and eglDestroyContext
 * calls.
 *
 *
 * This interface must be implemented by clients wishing to call
 * {@link GLWallpaperService#setEGLContextFactory(EGLContextFactory)}
 */
public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig);
 
        void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context);
}