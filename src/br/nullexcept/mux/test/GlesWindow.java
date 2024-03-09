package br.nullexcept.mux.test;

import br.nullexcept.mux.renderer.texel.CanvasTexel;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.Configuration;

import static org.lwjgl.glfw.GLFW.*;

public abstract class GlesWindow {
    private long window;
    private int[][] size = new int[2][1];
    public GlesWindow(){
    }

    public void init(){
        new Thread(()->{
            glfwInit();
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_ES_API);

            window = glfwCreateWindow(512,512,"OpenGL-ES", 0,0);
            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);
            Configuration.OPENGLES_EXPLICIT_INIT.set(false);
            GLES.createCapabilities();
            CanvasTexel.initialize();
            create();
            while (!glfwWindowShouldClose(window)){
                glfwGetWindowSize(window, size[0], size[1]);
                draw();
                glfwSwapBuffers(window);
                glfwPollEvents();
            }
            System.err.println("Exit...");
            dispose();
            CanvasTexel.destroy();
            glfwTerminate();
            System.gc();
            System.exit(0);
        }).start();
    }

    public int getWidth(){
        return size[0][0];
    }
    public int getHeight(){
        return size[1][0];
    }

    protected abstract void create();
    protected abstract void draw();
    protected abstract void dispose();
}
