package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.renderer.texel.CanvasTexel;
import br.nullexcept.mux.renderer.texel.GLTexel;

import java.util.ArrayList;
import java.util.HashMap;

public class RootViewGroup extends ViewGroup {
    private final HashMap<Integer, RenderObject> renders = new HashMap<>();
    private final CanvasTexel rootCanvas;
    private final RootDrawer drawer = new RootDrawer();

    public RootViewGroup(Context context) {
        super(context);
    }

    {
        rootCanvas = new CanvasTexel(512,512);
        getBounds().set(0,0,512,512);
    }

    @Override
    protected void measure() {
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
    public void invalidate() { }

    @Override
    protected void onTreeChanged() {
        super.onTreeChanged();
        mountRenderBag();
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(rootCanvas.getWidth(), rootCanvas.getHeight());
    }

    @Override
    public CanvasProvider getProvider() {
        return drawer;
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

    private void invalidateAll(){
        for (RenderObject render: renders.values()){
            render.view.requiresDraw = true;
        }
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
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    public void draw() {
        measure();
        invalidateAll();
        drawer.drawInternal(rootCanvas, this);
    }

    @Override
    protected void requestLayout() {
        for (View view: getChildren())
            view.measure();
        for (View view: getChildren())
            view.measureBounds();
    }

    public CanvasTexel getCanvas() {
        return rootCanvas;
    }

    private class RootDrawer implements CanvasProvider {

        private void drawInternal(CanvasTexel canvas, View view){
            Rect bounds = view.getBounds();
            if (bounds.width() != canvas.getWidth() || bounds.height() != canvas.getHeight()) {
                System.err.println("RESIZE CANVAS OF: ");
                canvas.getFramebuffer().resize(bounds.width(), bounds.height());
            }
            canvas.begin();
            canvas.reset();
            view.onDraw(canvas);
            if (view instanceof ViewGroup){
                canvas.end();
                for (View child: ((ViewGroup) view).getChildren()){
                    if (child.isVisible()){
                        if (child.requiresDraw) {
                            this.draw(child);
                        }
                        Rect childBounds = child.getBounds();
                        CanvasTexel texel = renders.get(child.hashCode()).canvas;
                        canvas.getFramebuffer().bind();
                        GLTexel.drawTexture(childBounds.left, childBounds.top,childBounds.width(),childBounds.height(), texel.getFramebuffer().getTexture());
                        canvas.getFramebuffer().unbind();
                    } else {
                        System.out.println(child.getBounds().toString());
                    }
                }
                canvas.begin();
            }
            view.onDrawForeground(canvas);
            view.requiresDraw = false;
            canvas.end();
        }

        @Override
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