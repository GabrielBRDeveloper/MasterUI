package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;

import java.util.Objects;

public class View {
    private static int currentHash = 0;
    private ViewGroup parent;
    private final Rect bounds = new Rect();
    private boolean focusable;
    private boolean touchable;
    private final Point measured = new Point();
    private final int hashCode = hash();
    private Object tag;
    private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public View(Context context) {
    }

    public void draw(Canvas canvas) {
    }

    public void onDraw(Canvas canvas) {
    }

    public ViewGroup getParent() {
        return null;
    }

    protected void measure() {
        Point location = parent.getChildLocation(this);
        { // Measure sizes
            int w = 1;
            int h = 1;
            if (params.width >= 0) {
                w = params.width;
            } else if (params.height == ViewGroup.LayoutParams.MATCH_PARENT){
                w = Math.max(0, parent.getMeasuredWidth() - location.x);
            }

            if (params.height >= 0){
                h = params.height;
            } else if (params.height == ViewGroup.LayoutParams.MATCH_PARENT){
                h = Math.max(0, parent.getMeasuredHeight() - location.y);
            }
            onMeasure(w,h);
        }
    }

    public <T extends View> T findViewByTag(Object tag){
        if (tag != null && Objects.equals(tag, this.tag)){
            return (T)this;
        }
        return null;
    }

    protected final void measureBounds(){
        Point location = parent.getChildLocation(this);
    }

    protected void onMeasure(int width, int height){
        measured.set(width, height);
    }

    private void setParent(ViewGroup group) {
        parent = group;
    }

    public int getMeasuredWidth() {
        return measured.y;
    }

    public int getMeasuredHeight() {
        return measured.y;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return params;
    }

    public Rect getGlobalBounds() {
        return bounds;
    }

    public void invalidate() {
    }

    public boolean isVisible() {
        return parent != null && parent.isVisible() && bounds.width() > 0 && bounds.height() > 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof View && ((View) obj).hashCode == hashCode;
    }

    private static synchronized int hash() {
        currentHash++;
        return currentHash;
    }
}