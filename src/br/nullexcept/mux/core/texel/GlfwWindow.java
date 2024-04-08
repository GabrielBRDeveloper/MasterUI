package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Activity;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.input.CharEvent;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES20;

class GlfwWindow extends Window {
    private final long window;
    private final GlfwEventManager eventManager;
    private final Point windowSize = new Point();

    private WindowContainer container;
    private boolean destroyed = true;
    private boolean visible;
    private boolean running = false;
    private final Point position = new Point();
    private WindowObserver observer;

    public GlfwWindow() {
        window = GLFW.glfwCreateWindow(512, 512, "[title]", 0, C.GLFW_CONTEXT);
        eventManager = new GlfwEventManager(this);
    }

    long getAddress(){
        return window;
    }

    @Override
    public int getWidth() {
        return windowSize.x;
    }

    @Override
    public int getHeight() {
        return windowSize.y;
    }

    private void refresh() {
        int[][] buffer = new int[2][1];
        GLFW.glfwGetWindowSize(window, buffer[0], buffer[1]);
        windowSize.set(buffer[0][0], buffer[1][0]);;
        GLFW.glfwGetWindowPos(window, buffer[0], buffer[1]);
        position.set(buffer[0][0], buffer[1][0]);
    }

    private int frames = 0;
    private long last = System.currentTimeMillis();

    private void update() {
        if(destroyed)
            return;
        running = true;

        if (System.currentTimeMillis() - last >= 1000) {
            last = System.currentTimeMillis();
            frames = 0;
        }
        frames++;
        eventManager.runFrame();

        long time = System.currentTimeMillis();
        if (isVisible()) {
            int ow = windowSize.x;
            int oh = windowSize.y;
            refresh();
            if (ow != windowSize.x || oh != windowSize.y) {
                onResize(windowSize.x, windowSize.y);
            }
            container.drawFrame();
            GLFW.glfwSwapBuffers(C.GLFW_CONTEXT);
            GLFW.glfwMakeContextCurrent(window);
            GLES.glViewport(0, 0, ow, oh);
            GLES.glClearColor(0,0,0,1);
            GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
            GLTexel.drawTexture(0, 0, ow, oh, container.getCanvas().getFramebuffer().getTexture());
            GLFW.glfwSwapBuffers(window);
            GLFW.glfwMakeContextCurrent(C.GLFW_CONTEXT);
        }
        if (GLFW.glfwWindowShouldClose(window)) {
            destroy();
        }
        time = System.currentTimeMillis() - time;
        // Window with focus = 60fps | without focus = 24fps
        time = Math.max(0, (isFocused() ? 16 : 41) - time);
        if (!destroyed) {
            Looper.getMainLooper().postDelayed(this::update, time);
        } else {
            running = false;
        }
    }

    private void onResize(int width, int height) {
        container.resize(width, height);
        container.invalidateAll();
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
        if (container != null){
            container.dispose();
            container= null;
        }
        container = new WindowContainer(view.getContext(), this);
        container.addChild(view);
    }

    public void onMouseEvent(MouseEvent event) {
        container.performInputEvent(event);
    }

    public void onKeyEvent(KeyEvent event) {
        container.performInputEvent(event);
    }

    public void onCharEvent(CharEvent charEvent) {
        container.performInputEvent(charEvent);
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

        GLFW.glfwSetWindowPos(window, position.x, position.y);
    }

    @Override
    public void setWindowObserver(WindowObserver observer) {
        this.observer = observer;
    }

    @Override
    public void create() {
        if(!destroyed){
            throw new RuntimeException("Window already created");
        }
        destroyed = false;
        windowSize.set(-1,-1);
        observer.onCreated();
        setVisible(true);
        if (!running){
            update();
        }
    }

    public void destroy() {
        setVisible(false);
        destroyed = true;

        if (observer != null){
            observer.onDestroy();
        }

        if (container != null ){
            container.dispose();
            container.removeAllViews();
        }

        container = null;
        observer = null;
    }

    public void onMouseMoved(MotionEvent event) {
        container.performInputEvent(event);
        View child = container.getChildAt((int)event.getX(), (int)event.getY());
        if (child != null){
            GLFW.glfwSetCursor(window, TexelAPI.getCursorPointer(child.getPointerIcon().getModel()));
        }
    }
}