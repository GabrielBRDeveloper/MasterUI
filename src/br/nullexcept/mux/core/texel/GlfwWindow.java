package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.hardware.CharEvent;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;
import org.lwjgl.glfw.GLFW;

class GlfwWindow extends Window {
    private final long window;
    private final GlfwEventManager eventManager;
    private int[][] sizes = new int[2][1];
    private WindowContainer container;
    private boolean destroyed;
    private boolean visible;
    private boolean initialized = false;
    private final Activity activity;
    private char lastCharacter;

    public GlfwWindow(Activity context) {
        activity = context;
        window = GLFW.glfwCreateWindow(512, 512, "[title]", 0, C.GLFW_CONTEXT);
        container = new WindowContainer(context, this);
        eventManager = new GlfwEventManager(this);
        Looper.getMainLooper().postDelayed(this::update, 10);
    }

    long getAddress(){
        return window;
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

    private void update() {
        if (System.currentTimeMillis() - last >= 1000) {
            last = System.currentTimeMillis();
            frames = 0;
        }
        frames++;
        eventManager.runFrame();
        long time = System.currentTimeMillis();
        if (isVisible()) {
            if (!initialized) {
                activity.onCreate();
                initialized = true;
            }
            container.drawFrame();
            getWidth();
            if (ow != sizes[0][0] || oh != sizes[1][0]) {
                ow = sizes[0][0];
                oh = sizes[1][0];
                container.resize(ow, oh);
            }
            GLFW.glfwSwapBuffers(C.GLFW_CONTEXT);
            GLFW.glfwMakeContextCurrent(window);
            GLES.glViewport(0, 0, ow, oh);
            GLTexel.drawTexture(0, 0, ow, oh, container.getCanvas().getFramebuffer().getTexture());
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwMakeContextCurrent(C.GLFW_CONTEXT);
        }
        if (GLFW.glfwWindowShouldClose(window)) {
            setVisible(false);
            destroyed = true;
            activity.onDestroy();
            container.removeAllViews();
            container.dispose();
        }
        time = System.currentTimeMillis() - time;
        // Window with focus = 60fps | without focus = 24fps
        time = Math.max(0, (isFocused() ? 16 : 41) - time);
        if (!destroyed) {
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
        return container.getChildrenCount() == 0 ? null : container.getChildAt(0);
    }

    @Override
    public void setContentView(View view) {
        container.addChild(view);
    }

    public void onMouseEvent(MouseEvent event) {
        container.sendEvent(event);
    }

    public void onKeyEvent(KeyEvent event) {
        container.sendEvent(event);
    }

    public void onCharEvent(CharEvent charEvent) {
        container.sendEvent(charEvent);
    }

    public boolean isFocused(){
        return GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }

    @Override
    public void setVisible(boolean visible) {
        if (isVisible() == visible) {
            return;
        }
        this.visible = visible;
        if (visible) {
            GLFW.glfwShowWindow(window);
        } else {
            GLFW.glfwHideWindow(window);
        }
    }
}