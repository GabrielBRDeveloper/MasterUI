package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES20;

class GlfwWindow extends Window {
    private final long window;
    private int[][] sizes = new int[2][1];
    private RootViewGroup anchor;
    private boolean destroyed;
    private boolean visible;
    private boolean initialized = false;
    private final Activity activity;

    public GlfwWindow(Activity context){
        activity = context;
        window = GLFW.glfwCreateWindow(512,512, "[title]",0, C.GLFW_CONTEXT);
        anchor  = new RootViewGroup(context, this);
        Looper.getMainLooper().postDelayed(this::update, 10);
    }

    @Override
    public int getWidth() {
        GLFW.glfwGetWindowSize(window, sizes[0], sizes[1]);
        return sizes[0][0];
    }

    @Override
    public int getHeight() {
        GLFW.glfwGetWindowSize(window, sizes[0], sizes[1]);
        return sizes[1][0];
    }

    private int ow = -1, oh = -1, frames = 0;
    private long last = System.currentTimeMillis();
    private void update(){
        if (System.currentTimeMillis() - last >= 1000){
            last = System.currentTimeMillis();
            System.out.println("Window ["+window+"] fps: "+frames);
            frames = 0;
        }
        frames++;
        long time = System.currentTimeMillis();
        if (isVisible()) {
            if (!initialized){
                activity.onCreate();
                initialized = true;
            }
            anchor.drawFrame();
            GLFW.glfwSwapBuffers(C.GLFW_CONTEXT);
            GLFW.glfwMakeContextCurrent(window);
            getWidth();
            if (ow != sizes[0][0] && oh != sizes[1][0]){
                ow = sizes[0][0];
                oh = sizes[1][0];
                anchor.resize(ow, oh);
            }
            GLES.glViewport(0,0,ow, oh);
            GLES.glClearColor(1.0f, 1.0f, 1.0f,1.0f);
            GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES.GL_DEPTH_BUFFER_BIT);
            GLTexel.drawTexture(0,0, ow, oh, anchor.getCanvas().getFramebuffer().getTexture());
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwMakeContextCurrent(C.GLFW_CONTEXT);
            if (GLFW.glfwWindowShouldClose(window)){
                setVisible(false);
                destroyed = true;
                activity.onDestroy();
            }
        }
        time = System.currentTimeMillis() - time;
        time = Math.max(0, 16-time); //60FPS
        if (!destroyed){
            Looper.getMainLooper().postDelayed(this::update, time);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, title);
    }

    @Override
    public void setResizable(boolean enable) {
        GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_RESIZABLE, enable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    @Override
    public View getContentView() {
        return anchor.getChildrenCount() == 0 ? null : anchor.getChildAt(0);
    }

    @Override
    public void setContentView(View view) {
        anchor.addChild(view);
    }

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible){
            return;
        }
        this.visible = visible;
        if (visible){
            GLFW.glfwShowWindow(window);
        } else {
            GLFW.glfwHideWindow(window);
        }
    }
}
