package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.C;
import br.nullexcept.mux.app.Looper;
import br.nullexcept.mux.graphics.Drawable;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.graphics.fonts.Typeface;
import br.nullexcept.mux.hardware.GLES;
import br.nullexcept.mux.input.CharEvent;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengles.GLES20;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

class GlfwWindow extends Window {
    private final long window;
    private final GlfwEventManager eventManager;
    private final Size windowSize = new Size(512,512);

    private WindowContainer container;
    private boolean destroyed = true;
    private boolean visible;
    private boolean running = false;
    private final Point position = new Point();
    private WindowObserver observer;
    private CharSequence title;

    public GlfwWindow() {
        window = GLFW.glfwCreateWindow(512, 512, title = "[MasterUI:Window]", 0, C.GLFW_CONTEXT);
        eventManager = new GlfwEventManager(this);
        GLFW.glfwIconifyWindow(window);
        setMinimumSize(256,256);
        center();
    }

    private void center() {
        {
            int[][] sizes = new int[4][1];
            long monitor = GLFW.glfwGetPrimaryMonitor();
            GLFW.glfwGetMonitorWorkarea(monitor, sizes[0], sizes[1], sizes[2],sizes[3]);
            int x = sizes[0][0] + (sizes[2][0]/2) - 256;
            int y = sizes[1][0] + (sizes[3][0]/2) - 256;

            position.set(x,y);
        }

        align();
    }

    long getAddress(){
        return window;
    }

    @Override
    public int getWidth() {
        return windowSize.width;
    }

    @Override
    public int getHeight() {
        return windowSize.height;
    }

    private void refresh() {
        int[][] buffer = new int[2][1];
        GLFW.glfwGetWindowSize(window, buffer[0], buffer[1]);
        windowSize.set(buffer[0][0], buffer[1][0]);;
        GLFW.glfwGetWindowPos(window, buffer[0], buffer[1]);
        position.set(buffer[0][0], buffer[1][0]);
    }

    private long[] times = new long[8];
    private long last = System.currentTimeMillis();

    private void update() {
        if(destroyed)
            return;
        running = true;

        if (System.currentTimeMillis() - last >= 1000) {
            last = System.currentTimeMillis();
            times[5] = times[0];
            times[6] = times[1] / times[0];

            times[0] = 0;
            times[1] = 0;
        }
        times[0]++;
        eventManager.runFrame();

        long time = System.currentTimeMillis();
        if (isVisible() && container != null) {
            long begin = System.currentTimeMillis();
            refresh();
            container.resize(windowSize.width, windowSize.height);

            container.drawFrame();
            GLFW.glfwSwapBuffers(C.GLFW_CONTEXT);
            GLFW.glfwMakeContextCurrent(window);
            glfwSwapInterval(0); //NEED THAT FOR NOT SLOW MAIN LOOP
            GLES.glViewport(0, 0, windowSize.width, windowSize.height);
            GLES.glClearColor(0,0,0,1);
            GLES.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
            GLTexel.drawTexture(0, 0, windowSize.width, windowSize.height, container.getCanvas().getFramebuffer().getTexture());

            times[1] += System.currentTimeMillis() - begin;
            if (C.Flags.DEBUG_OVERLAY) {
                drawDebug();
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwMakeContextCurrent(C.GLFW_CONTEXT);
        }
        if (GLFW.glfwWindowShouldClose(window)) {
            destroy();
        }
        time = System.currentTimeMillis() - time;
        // Window with focus = 60fps | without focus = 24fps
        time = Math.max(0, (isFocused() ? 16 : 41) - time);
        if(C.Flags.FULL_DRAW) {
            time = 1;
        }
        if (!destroyed) {
            Looper.getMainLooper().postDelayed(this::update, time);
        } else {
            running = false;
        }
    }

    private final NVGColor color1 = NVGColor.create();
    private final NVGColor color2 = NVGColor.create();
    private void drawDebug() {
        NanoVG.nvgBeginFrame(C.VG_CONTEXT,windowSize.width, windowSize.height,1);
        NanoVG.nvgFontFaceId(C.VG_CONTEXT, Typeface.DEFAULT.hashCode());
        NanoVG.nvgFontSize(C.VG_CONTEXT, 16);

        NanoVG.nvgBeginPath(C.VG_CONTEXT);
        NanoVG.nvgRGBAf(0,0,0,.5f, color2);
        NanoVG.nvgFillColor(C.VG_CONTEXT, color2);
        NanoVG.nvgRect(C.VG_CONTEXT, 10, 10, 160,72);
        NanoVG.nvgFill(C.VG_CONTEXT);
        NanoVG.nvgClosePath(C.VG_CONTEXT);

        NanoVG.nvgRGBAf(1,1,1,1, color1);
        NanoVG.nvgFillColor(C.VG_CONTEXT, color1);
        NanoVG.nvgText(C.VG_CONTEXT,15,32,"FPS: "+times[5]);
        NanoVG.nvgText(C.VG_CONTEXT,15,48,"DRAW TIME: "+times[6]+"ms");
        NanoVG.nvgText(C.VG_CONTEXT,15,64,"VIEWS: "+viewCount(container));
        NanoVG.nvgEndFrame(C.VG_CONTEXT);
    }

    private int viewCount(ViewGroup container) {
        int count = 0;
        for (View view: container.getChildren()) {
            if (view instanceof ViewGroup)
                count += viewCount((ViewGroup) view);
            count++;
        }
        return count;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(window, this.title = title);
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
            container.removeAllViews();
            container.dispose();
            container= null;
        }
        eventManager.clear();
        if (view == null)
            return;

        container = new WindowContainer(view.getContext(), this);
        container.addChild(view);
    }

    @Override
    public void setSize(int width, int height) {
        // Keep position aspect
        position.x += (windowSize.width-width)/2;
        position.y += (windowSize.height-height)/2;

        windowSize.set(width, height);
        align();
    }

    @Override
    public void setMinimumSize(int width, int height) {
        GLFW.glfwSetWindowSizeLimits(window, width,height,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void onMouseEvent(MouseEvent event) {
        if (container != null) {
            container.performInputEvent(event);
        }
    }

    public void onKeyEvent(KeyEvent event) {
        if (container != null) {
            container.performInputEvent(event);
        }
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
            align();
            GLFW.glfwShowWindow(window);
        } else {
            refresh();
            align();
            GLFW.glfwHideWindow(window);
        }
    }

    private void align() {
        windowSize.set(Math.max(1, windowSize.width), Math.max(1, windowSize.height));
        position.set(Math.max(0, position.x), Math.max(0, position.y));
        GLFW.glfwSetWindowSize(window, windowSize.width, windowSize.height);
        GLFW.glfwSetWindowPos(window, position.x, position.y);
    }

    @Override
    public void setWindowObserver(WindowObserver observer) {
        this.observer = observer;
    }

    @Override
    public WindowObserver getWindowObserver() {
        return observer;
    }

    @Override
    public void create() {
        if(!destroyed){
            throw new RuntimeException("Window already created");
        }
        destroyed = false;
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

    @Override
    public void setIcon(Drawable icon) {
        int iconSize = 128;

        icon.setBounds(new Rect(0,0,iconSize,iconSize));

        CanvasTexel canvas = new CanvasTexel(iconSize, iconSize);
        canvas.begin();
        icon.draw(canvas);
        canvas.end();
        ByteBuffer buffer = BufferUtils.createByteBuffer((iconSize*iconSize)*4);

        canvas.getFramebuffer().bind();
        GLES.glReadPixels(0,0, iconSize,iconSize, GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,buffer);
        canvas.getFramebuffer().unbind();
        canvas.dispose();

        new Thread(()->{
            buffer.rewind();
            ByteBuffer flipY = BufferUtils.createByteBuffer(buffer.capacity());
            flipY.position(0);
            byte[] row = new byte[iconSize*4];
            for (int i = 0; i < iconSize; i++) {
                buffer.position(
                        (buffer.capacity()) - (row.length * (i+1))
                );
                buffer.get(row);
                flipY.position(row.length*i);
                flipY.put(row);
            }

            flipY.flip();

            Looper.getMainLooper().post(()->{
                if (!destroyed) {
                    GLFWImage ic = GLFWImage.malloc();
                    ic.set(iconSize, iconSize, flipY);
                    GLFWImage.Buffer stack = GLFWImage.malloc(1);
                    stack.put(0, ic);
                    GLFW.glfwSetWindowIcon(window, stack);
                }
            });
        }).start();
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    @Override
    public Size getSize() {
        return new Size(windowSize.width, windowSize.height);
    }

    public void onMouseMoved(MotionEvent event) {
        if (container == null)
            return;

        container.performInputEvent(event);
        View child = container.getChildAt((int)event.getX(), (int)event.getY());
        if (child != null){
            GLFW.glfwSetCursor(window, TexelAPI.getCursorPointer(child.getPointerIcon().getModel()));
        }
    }
}