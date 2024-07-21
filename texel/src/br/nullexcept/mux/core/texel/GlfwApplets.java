package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Applet;
import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.applets.ClipboardApplet;
import br.nullexcept.mux.app.applets.DisplayApplet;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.res.DisplayInformation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class GlfwApplets {
    public static void initialize() {
        HashMap<String, Applet> list = TexelAPI.applets;
        list.put(Context.DISPLAY_APPLET, new DisplayApplet() {
            @Override
            public DisplayInformation getPrimaryDisplay() {
                return new GlfwDisplay(GLFW.glfwGetPrimaryMonitor());
            }

            @Override
            public List<DisplayInformation> getDisplays() {
                throw new RuntimeException("Not implemented support for multi-screen");
            }
        });
        list.put(Context.CLIPBOARD_APPLET, new ClipboardApplet() {
            @Override
            public String getContent() {
                return GLFW.glfwGetClipboardString(C.GLFW_CONTEXT);
            }

            @Override
            public boolean hasContent() {
                String content = getContent();
                return content != null && content.length() > 0;
            }

            @Override
            public void setContent(String content) { 
                GLFW.glfwSetClipboardString(C.GLFW_CONTEXT, content);
            }
        });
    }
}

class GlfwDisplay implements DisplayInformation {
    private final long id;

    GlfwDisplay(long id) {
        this.id = id;
    }

    @Override
    public Size getPhysicalSize() {
        int[][] size = new int[2][1];
        GLFW.glfwGetMonitorPhysicalSize(id, size[0],size[1]);
        return new Size(size[0][0], size[1][0]);
    }

    @Override
    public Size getResolution() {
        int[][] size = new int[3][1];
        GLFW.glfwGetMonitorWorkarea(id, size[2],size[2],size[0], size[1]);
        return new Size(size[0][0], size[1][0]);
    }

    @Override
    public String getMonitorId() {
        return String.valueOf(id);
    }

    @Override
    public String getName() {
        return GLFW.glfwGetMonitorName(id);
    }

    @Override
    public float getMonitorScale() {
        float[] scale = new float[1];
        GLFW.glfwGetMonitorContentScale(id, scale,scale);
        return scale[0];
    }
}
