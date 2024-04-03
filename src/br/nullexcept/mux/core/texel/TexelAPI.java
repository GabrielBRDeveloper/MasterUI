package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Applet;
import br.nullexcept.mux.view.PointerIcon;
import br.nullexcept.mux.view.Window;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class TexelAPI {
    static final HashMap<String, Applet> applets = new HashMap<>();
    private static final HashMap<PointerIcon.Model, Long> pointers = new HashMap<>();

    public static void initialize() {
        VgTexel.initialize();
        pointers.put(PointerIcon.Model.ARROW, GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR));
        pointers.put(PointerIcon.Model.HAND, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR));
        pointers.put(PointerIcon.Model.RESIZE, GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR));
        pointers.put(PointerIcon.Model.TEXT_SELECTION, GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR));

        GlfwApplets.initialize();
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
}
