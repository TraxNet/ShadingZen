package org.traxnet.shadingzen.util.wallpaper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;


public class DefaultContextFactory implements EGLContextFactory {
 
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
                return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
                                new int[] { 0x3098, 2, EGL10.EGL_NONE });
        }
 
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                egl.eglDestroyContext(display, context);
        }
}