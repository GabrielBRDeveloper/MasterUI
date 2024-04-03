package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Applet;
import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.applets.ClipboardApplet;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

class GlfwApplets {
    public static void initialize() {
        HashMap<String, Applet> list = TexelAPI.applets;
        list.put(Context.CLIPBOARD_APPLET, new ClipboardApplet() {
            @Override
            public String getContent() {
                return GLFW.glfwGetClipboardString(C.GLFW_CONTEXT);
            }

            @Override
            public void setContent(String content) {
                GLFW.glfwSetClipboardString(C.GLFW_CONTEXT, content);
            }
        });
    }
}
