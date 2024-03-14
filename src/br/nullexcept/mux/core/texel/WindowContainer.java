package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.hardware.CharEvent;
import br.nullexcept.mux.input.Event;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WindowContainer extends ViewGroup {
    private static final int FLAG_REQUIRES_DRAW = 2;
    private static final int FLAG_FOCUSED = 4;

    private final HashMap<Integer, RenderObject> renders = new HashMap<>();
    private final CanvasTexel rootCanvas;
    private final ViewDrawer drawer = new ViewDrawer();
    private int focusedView = hashCode();

    public WindowContainer(Context context, Window window) {
        super(context);
        setBackground(new ColorDrawable(Color.BLACK));
        rootCanvas = new CanvasTexel(64,64);
        getBounds().set(0,0,64,64);
    }

    @Override
    public void measure() {
        getBounds().set(0,0,rootCanvas.getWidth(), rootCanvas.getHeight());
    }

    @Override
    public int getMeasuredHeight() {
        return rootCanvas.getHeight();
    }

    @Override
    public int getMeasuredWidth() {
        return rootCanvas.getWidth();
    }

    @Override
    protected void onTreeChanged() {
        super.onTreeChanged();
        mountRenderBag();
        requestFocus(this);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(rootCanvas.getWidth(), rootCanvas.getHeight());
    }

    private void mountRenderBag(ViewGroup group){
        for (View child: group.getChildren()){
            int hash = child.hashCode();
            if (!renders.containsKey(hash)){
                RenderObject render = new RenderObject(child);
                render.canvas = new CanvasTexel(1,1);
                renders.put(hash, render);
            }
            renders.get(hash).checked = true;
            if (child instanceof ViewGroup){
                mountRenderBag((ViewGroup) child);
            }
        }
    }

    @Override
    protected void requestFocus(View focused) {
        focused = focused == null ? this : focused;
        if (!focused.hasFlag(FLAG_FOCUSED)){
            if (renders.containsKey(focusedView)){
                View oldFocus = renders.get(focusedView).view;
                oldFocus.onFocusChanged(false);
            }
            focusedView = focused.hashCode();
            focused.onFocusChanged(true);
            focused.addFlag(FLAG_FOCUSED);
        }
    }

    void invalidateAll(){
        for (RenderObject render: renders.values()){
            render.view.addFlag(FLAG_REQUIRES_DRAW);
        }
        addFlag(FLAG_REQUIRES_DRAW);
    }

    private void mountRenderBag(){
        for (RenderObject render: renders.values())
            render.checked = false;

        mountRenderBag(this);
        ArrayList<RenderObject> renders = new ArrayList<>(this.renders.values());
        for (RenderObject render: renders){
            if (!render.checked){
                this.renders.remove(render.view.hashCode());
                render.canvas.dispose();
            }
        }
        invalidateAll();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    private long lastRefresh = 0;
    public void drawFrame() {
        if (System.currentTimeMillis() - lastRefresh >= 7000){
            invalidateAll();
            lastRefresh = System.currentTimeMillis();
        }
        if (hasFlag(FLAG_REQUIRES_DRAW)) {
            drawer.drawInternal(rootCanvas, this);
        }
    }

    @Override
    public void addChild(View view) {
        //For allow only 1 view
        removeAllViews();
        view.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        super.addChild(view);
    }

    @Override
    public void requestLayout() {
        for (View view: getChildren())
            view.measure();
        for (View view: getChildren())
            view.measureBounds();
    }

    public void dispose(){
        for (RenderObject render: renders.values()){
            render.canvas.dispose();
        }
        renders.clear();
    }

    public void sendEvent(Event event) {
        if (event instanceof MouseEvent){
            if(event.getTarget() == -1){
                dispatchEvent(event);
            } else {
                View target = renders.get(event.getTarget()).view;
                Rect visibleBounds = target.getVisibleBounds();
                ((MouseEvent) event).transform(-visibleBounds.left, -visibleBounds.top);
                target.dispatchEvent(event);
                ((MouseEvent) event).transform(visibleBounds.left, visibleBounds.top);
            }
        } else if ((event instanceof KeyEvent)||(event instanceof CharEvent)){
            if (focusedView != hashCode()){
                View focused = renders.get(focusedView).view;
                focused.dispatchEvent(event);
            }
        }
    }

    public CanvasTexel getCanvas() {
        return rootCanvas;
    }

    public void resize(int w, int h) {
        rootCanvas.getFramebuffer().resize(w, h);
        measure();
        requestLayout();
        invalidateAll();
    }

    private class ViewDrawer {

        private void drawInternal(CanvasTexel canvas, View view){
            Rect bounds = view.getBounds();
            if (bounds.width() != canvas.getWidth() || bounds.height() != canvas.getHeight()) {
                canvas.getFramebuffer().resize(bounds.width(), bounds.height());
            }
            canvas.begin();
            canvas.reset();
            view.onDraw(canvas);
            if (view instanceof ViewGroup){
                canvas.end();
                List<View> children = ((ViewGroup) view).getChildren();
                int drawables = 0;
                for (View child: children){
                    if (child.isVisible()) {
                        if (child.hasFlag(FLAG_REQUIRES_DRAW)) {
                            this.draw(child);
                        }
                        drawables++;
                    }
                }
                float[][] borders = new float[5][drawables];
                int[] textures = new int[drawables];
                int index = 0;
                for (View child: children){
                    if (child.isVisible()){
                        Rect childBounds = child.getBounds();
                        CanvasTexel texel = renders.get(child.hashCode()).canvas;
                        borders[0][index] = childBounds.left;
                        borders[1][index] = childBounds.top;
                        borders[2][index] = childBounds.width();
                        borders[3][index] = childBounds.height();
                        borders[4][index] = child.getAlpha();
                        textures[index] = texel.getFramebuffer().getTexture().getTexture();
                        index++;
                    }
                }
                canvas.getFramebuffer().bind();
                GLTexel.drawViewLayers(borders[0], borders[1], borders[2], borders[3],textures, borders[4]);
                canvas.getFramebuffer().unbind();
                canvas.begin();
            }
            view.onDrawForeground(canvas);
            view.subFlag(FLAG_REQUIRES_DRAW);
            canvas.end();
        }

        public void draw(View view) {
            if (!view.isVisible()){
                return;
            }
            RenderObject render = renders.get(view.hashCode());
            drawInternal(render.canvas, view);
        }
    }

    private class RenderObject {
        private CanvasTexel canvas;
        private final View view;
        private boolean checked;

        private RenderObject(View view) {
            this.view = view;
        }
    }
}
