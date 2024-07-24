package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Applet;
import br.nullexcept.mux.app.base.CoreBoostrap;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.view.PointerIcon;
import br.nullexcept.mux.view.Window;
import org.lwjgl.egl.EGL;
import org.lwjgl.egl.EGL10;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeEGL;
import org.lwjgl.opengles.GLES;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

class TexelAPI implements CoreBoostrap {
    static final HashMap<String, Applet> applets = new HashMap<>();
    private static final HashMap<PointerIcon.Model, Long> pointers = new HashMap<>();

    public static void initialize() {
        VgTexel.initialize();
        GlfwApplets.initialize();
        pointers.put(PointerIcon.Model.ARROW, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
        pointers.put(PointerIcon.Model.HAND, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
        pointers.put(PointerIcon.Model.RESIZE, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
        pointers.put(PointerIcon.Model.TEXT_SELECTION, GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));
    }

    public static void destroy() {
        VgTexel.destroy();
    }

    protected static long getCursorPointer(PointerIcon.Model model){
        return pointers.get(model);
    }

    public static Window createWindow() {
        return new GlfwWindow();
    }

    public static Map<String, Applet> obtainApplets() {
        return applets;
    }

    @Override
    public void boot() {
        glfwInit();
        setupEGL();

        long window = glfwCreateWindow(1,1,"[MasterUI:Core]",0, 0);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);

        C.GLFW_CONTEXT = window;
        GLES.createCapabilities();
        TexelAPI.initialize();
        Looper.getCurrentLooper().postDelayed(this::loop, 1);
    }

    private void loop() {
        glfwPollEvents();
        Looper.getCurrentLooper().postDelayed(this::loop,1);
    }

    private static void setupEGL() {
        glfwDefaultWindowHints();
        if (C.Config.SET_WINDOW_GL_HINT) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, C.Config.WINDOW_GL_VERSION[0]);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, C.Config.WINDOW_GL_VERSION[1]);
            glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_EGL_CONTEXT_API);
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);

            long display = GLFWNativeEGL.glfwGetEGLDisplay();
            try {// Setup EGL
                int[][] version = new int[2][1];
                EGL10.eglInitialize(display, version[0], version[1]);
                EGL.createDisplayCapabilities(display, version[0][0], version[1][0]);
            } catch (Exception e) {}
        }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    }

    @Override
    public void finish() {
        TexelAPI.destroy();
        glfwTerminate();
    }

    @Override
    public Window makeWindow() {
        return createWindow();
    }

    @Override
    public Map<? extends String, ? extends Applet> getSystemApplets() {
        return TexelAPI.obtainApplets();
    }
}
