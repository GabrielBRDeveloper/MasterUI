package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.res.AttributeList;

import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View {
    private final ArrayList<View> children = new ArrayList<>();
    private final Rect rect = new Rect();

    public ViewGroup(Context context) {
        super(context);
    }

    public ViewGroup(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    protected Point getChildLocation(View view) {
        return new Point(getPaddingLeft(), getPaddingTop());
    }

    public void requestLayout() {
        if (getParent() != null) {
            getParent().requestLayout();
            invalidate();
        } else {
            measure();
        }
    }

    protected int measureSpec(int parent, int spec, int wrap, int padding) {
        if (spec == LayoutParams.WRAP_CONTENT) {
            return wrap + padding;
        } else if (spec == LayoutParams.MATCH_PARENT) {
            return Math.max(parent, 0);
        } else {
            return spec;
        }
    }

    protected boolean measureChild(View child) {
        LayoutParams params = child.getLayoutParams();
        if (child instanceof ViewGroup) {
            ((ViewGroup) child).measureChildren();
        }
        Size size = child.onMeasureContent(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(), getMeasuredHeight()-getPaddingTop()-getPaddingBottom());
        Point pos = getChildLocation(child);

        int left = pos.x - getPaddingLeft();
        int top  = pos.y - getPaddingTop();

        int w = measureSpec(getMeasuredWidth()-getPaddingLeft()-getPaddingRight()-left, params.width, size.width, child.getPaddingLeft() + child.getPaddingRight());
        int h = measureSpec(getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-top, params.height, size.height, child.getPaddingTop() + child.getPaddingBottom());
        if (w != child.getMeasuredWidth() || h != child.getMeasuredHeight()) {
            child.onMeasure(w,h);
            return true;
        }
        return false;
    }

    protected boolean measureChildren(){
        if (children == null)
            return false;

        boolean changed = false;
        for (View child : children) {
            changed |= measureChild(child);
        }

        for (View child: children) {
            child.measureBounds();
        }
        return changed;
    }

    @Override
    public void measure() {
        ViewGroup parent = getParent();
        if (parent == null)
            return;

        parent.measureChild(this);
        while (measureChildren()) {
            measure();
        }
        parent.measureChild(this);
        measureBounds();
    }

    @Override
    protected boolean dispatchMouseEvent(MouseEvent mouseEvent) {
        boolean handle = false;
        for (int i = children.size() - 1; i >= 0; i--){
            View child = children.get(i);
            Rect bounds = child.getBounds();
            if (bounds.inner(mouseEvent.getX(), mouseEvent.getY())){
                final int bx = bounds.left;
                final int by = bounds.top;
                mouseEvent.transform(-bx, -by);
                handle = child.dispatchMouseEvent(mouseEvent);
                mouseEvent.transform(bx, by);
            }
            if (handle)
                break;
        }
        return handle || super.dispatchMouseEvent(mouseEvent);
    }

    public final View getChildAt(int x,int y){
        Rect bounds = getBounds();
        if (x >= 0 && y >= 0 && x <= bounds.width() && y <= bounds.height()){
            for (int i = children.size()-1; i >= 0; i--){
                View child = children.get(i);
                Rect cb = child.getBounds();
                if (cb.inner(x,y)){
                    if (child instanceof ViewGroup){
                        return ((ViewGroup) child).getChildAt(x-cb.left, y-cb.top);
                    } else {
                        return child;
                    }
                }
            }
            return this;
        }
        return null;
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        Size size = new Size();
        for (View child: children){
            int x = Math.max(0, getChildLocation(child).x - getPaddingLeft());
            size.width = Math.max(x + child.getMeasuredWidth(), size.width);
            int y = Math.max(0, getChildLocation(child).y - getPaddingTop());
            size.height = Math.max(y+child.getMeasuredHeight(), size.height);
        }
        return size;
    }

    public int getChildrenCount(){
        return children.size();
    }

    public List<View> getChildren(){
        return new ArrayList<>(children);
    }

    public int getChildIndex(View child){
        return children.indexOf(child);
    }

    public <T extends View> T getChildAt(int index){
        return (T) children.get(index);
    }

    public void addChild(View view){
        children.add(view);
        view.setParent(this);
        onChildAdded(view);
        view.addFlag(View.FLAG_REQUIRES_DRAW);
        requestLayout();
        notifyTreeChanged();
    }

    protected void requestFocus(View focused){
        if (getParent() != null){
            getParent().requestFocus(focused);
        }
    }

    public void removeChild(View view){
        if (children.contains(view)){
            children.remove(view);
            view.setParent(null);
            notifyTreeChanged();
            onChildRemoved(view);
        }
    }

    public void removeAllViews(){
        while (children.size() > 0){
            removeChild(children.get(0));
        }
    }

    private void onChildRemoved(View view) {
    }

    private void notifyTreeChanged(){
        if (getParent() != null){
            getParent().notifyTreeChanged();
        }
        onTreeChanged();
    }

    @Override
    protected void onViewRootChanged() {
        super.onViewRootChanged();
        for (View child: children) {
            child.onViewRootChanged();
        }
    }

    protected void onChildAdded(View view){
        requestLayout();
    }

    protected void onTreeChanged(){
        invalidate();
    }

    @Override
    public <T extends View> T findViewByTag(Object tag) {
        T self = super.findViewByTag(tag);
        if (self == null){
            for (View child: children){
                if ((self = child.findViewByTag(tag)) != null){
                    return self;
                }
            }
        }
        return self;
    }

    @Override
    public <T extends View> T findViewById(String tag) {
        T self = super.findViewById(tag);
        if (self == null){
            for (View child: children){
                if ((self = child.findViewById(tag)) != null){
                    return self;
                }
            }
        }
        return self;
    }

    public static class LayoutParams {
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;
        public int width;
        public int height;

        public LayoutParams(int width, int height){
            this.width = width;
            this.height = height;
        }

        public void from(LayoutParams params) {
            this.width = params.width;
            this.height = params.height;
        }

        public boolean hasWrap(){
            return width == WRAP_CONTENT || height == WRAP_CONTENT;
        }
    }
}
