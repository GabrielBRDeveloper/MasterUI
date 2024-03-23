package br.nullexcept.mux.core.texel;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Color;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.input.*;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;
import br.nullexcept.mux.view.Window;

import java.util.ArrayList;
import java.util.HashMap;

public class WindowContainer extends ViewGroup {
    static final int FLAG_REQUIRES_DRAW = 2;
    private static final int FLAG_FOCUSED = 4;

    private final HashMap<Integer, RenderCache> renders = new HashMap<>();
    private final CanvasTexel rootCanvas;
    private final ViewRenderer drawer;

    private int focusedView = hashCode();

    public WindowContainer(Context context, Window window) {
        super(context);
        setBackground(new ColorDrawable(Color.BLACK));
        rootCanvas = new CanvasTexel(64,64);
        getBounds().set(0,0,64,64);
        drawer = new ViewRenderer(renders);
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
        requestLayout();
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(rootCanvas.getWidth(), rootCanvas.getHeight());
    }

    private void mountRenderBag(ViewGroup group){
        for (View child: group.getChildren()){
            int hash = child.hashCode();
            if (!renders.containsKey(hash)){
                RenderCache render = new RenderCache(child);
                render.canvas = new CanvasTexel(1,1);
                renders.put(hash, render);
            }
            renders.get(hash).valid = true;
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
                oldFocus.subFlag(FLAG_FOCUSED);
            }
            focusedView = focused.hashCode();
            focused.onFocusChanged(true);
            focused.addFlag(FLAG_FOCUSED);
        }
    }

    void invalidateAll(){
        for (RenderCache render: renders.values()){
            render.view.addFlag(FLAG_REQUIRES_DRAW);
        }
        addFlag(FLAG_REQUIRES_DRAW);
    }

    private void mountRenderBag(){
        for (RenderCache render: renders.values())
            render.valid = false;

        mountRenderBag(this);
        ArrayList<RenderCache> renders = new ArrayList<>(this.renders.values());
        for (RenderCache render: renders){
            if (!render.valid){
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
        invalidateAll();
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
        for (RenderCache render: renders.values()){
            render.canvas.dispose();
        }
        renders.clear();
    }

    public void performInputEvent(Event event) {
        if (event instanceof MouseEvent){
            performHover((MouseEvent)event);
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
        } else {
            dispatchEvent(event);
        }
    }

    private View lastHover;
    private void performHover(MouseEvent event) {
        View view = getChildAt((int)event.getX(), (int)event.getY());

        if (view != null) {
            if (view.equals(lastHover)) {
                return;
            }
            view.setHovered(true);
        }
        if (lastHover != null){
            lastHover.setHovered(false);
        }
        lastHover = view;
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

    @Override
    protected View getFocused() {
        if (renders.containsKey(focusedView)){
            return renders.get(focusedView).view;
        }
        return null;
    }
}
