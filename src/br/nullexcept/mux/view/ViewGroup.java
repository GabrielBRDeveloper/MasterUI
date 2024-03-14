package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
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
            measureBounds();
        }
    }

    @Override
    public void measure() {
        LayoutParams params = getLayoutParams();
        ViewGroup parent = getParent();
        if (parent == null)
            return;

        parent.getInternalSize(rect);

        int ow = getMeasuredWidth();
        int oh = getMeasuredHeight();
        Point location = parent.getChildLocation(this);
        int width, height;
        if (params.hasWrap()) {
            if (params.width == LayoutParams.WRAP_CONTENT) {
                width = calculateWidth();
            } else {
                width = params.width == LayoutParams.MATCH_PARENT ? rect.width() - location.x : params.width;
            }
            if (params.height == LayoutParams.WRAP_CONTENT) {
                height = calculateHeight();
            } else {
                height = params.height == LayoutParams.MATCH_PARENT ? rect.height() - location.y : params.height;
            }
        } else {
            width = params.width == LayoutParams.MATCH_PARENT ? rect.width() - location.x : params.width;
            height = params.height == LayoutParams.MATCH_PARENT ? rect.height() - location.y : params.height;
            width = Math.max(0, width);
            height = Math.max(0, height);
        }
        for (View child : children) {
            child.measure();
        }
        for (View child : children) {
            child.measureBounds();
        }
        if (width != ow || height != oh) {
            onMeasure(width, height);
            measure();
        }
    }

    @Override
    public boolean dispatchMouseEvent(MouseEvent mouseEvent) {
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
            if (handle)break;
        }
        return handle || super.dispatchMouseEvent(mouseEvent);
    }

    @Override
    protected int calculateWidth() {
        int width = 0;
        for (View child: children){
            width = Math.max(child.getMeasuredWidth(), width);
        }
        return width + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected int calculateHeight() {
        int height = 0;
        for (View child: children){
            height = Math.max(child.getMeasuredHeight(), height);
        }
        return height + getPaddingTop() + getPaddingBottom();
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
        notifyTreeChanged();
    }

    protected void requestFocus(View focused){
        if (getParent() != null){
            getParent().requestFocus(focused);
        }
    }

    public void removeAllViews(){
        while (children.size() > 0){
            View child = children.get(0);
            children.remove(0);
            child.setParent(null);
            notifyTreeChanged();
            onChildRemoved(child);
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

    public static class LayoutParams {
        public static final int MATCH_PARENT = -1;
        public static final int WRAP_CONTENT = -2;
        public int width;
        public int height;

        public LayoutParams(int width, int height){
            this.width = width;
            this.height = height;
        }

        public boolean hasWrap(){
            return width == WRAP_CONTENT || height == WRAP_CONTENT;
        }
    }
}
