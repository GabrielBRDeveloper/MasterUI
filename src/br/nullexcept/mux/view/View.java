package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Canvas;
import br.nullexcept.mux.graphics.Drawable;
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
    private final Rect rect = new Rect();
    private Object tag;
    private Drawable background;
    private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    boolean requiresDraw = true;

    public View(Context context) {
    }

    public void setBackground(Drawable background) {
        this.background = background;
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        rect.set(0,0, canvas.getWidth(), canvas.getHeight());
        if (background != null) {
            background.setBounds(rect);
            background.draw(canvas);
        }
    }

    public void onDrawForeground(Canvas canvas){

    }

    public ViewGroup getParent() {
        return parent;
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
        Point location = getParent().getChildLocation(this);
        bounds.set(location.x, location.y, location.x+getMeasuredWidth(), location.y+getMeasuredHeight());
    }

    protected void onMeasure(int width, int height){
        measured.set(width, height);
    }

    final void setParent(ViewGroup group) {
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

    public Rect getBounds() {
        return bounds;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public CanvasProvider getProvider(){
        return getParent().getProvider();
    }

    public void invalidate() {
        requiresDraw = true;
        if (getParent() != null) {
            getParent().invalidate();
        }
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

    public int hashCode(){
        return this.hashCode;
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        this.params = params;
        if (getParent() != null) {
            getParent().requestLayout();
        }
        invalidate();
    }

    protected interface CanvasProvider {
        void draw(View view);
    }
}