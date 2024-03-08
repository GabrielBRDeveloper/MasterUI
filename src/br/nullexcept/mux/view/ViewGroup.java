package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends View {
    private final ArrayList<View> children = new ArrayList<>();

    public ViewGroup(Context context) {
        super(context);
    }

    protected Point getChildLocation(View view) {
        return new Point(0, 0);
    }

    protected void requestLayout() {
        if (getParent() != null) {
            getParent().requestLayout();
        } else {
            measure();
            measureBounds();
        }
    }

    @Override
    protected void measure() {
        LayoutParams params = getLayoutParams();
        ViewGroup parent = getParent();
        Point location = parent.getChildLocation(this);

        if (params.width == LayoutParams.WRAP_CONTENT || params.height == LayoutParams.WRAP_CONTENT){
            //@TODO
        } else {
            int width = params.width == LayoutParams.MATCH_PARENT ? parent.getMeasuredWidth() - location.x : params.width;
            int height = params.height == LayoutParams.MATCH_PARENT ? parent.getMeasuredHeight() - location.y : params.height;
            width = Math.max(0, width);
            height = Math.max(0, height);
            onMeasure(width, height);
        }
        for (View child: children){
            child.measure();
        }
        for (View child: children){
            child.measureBounds();
        }
    }

    public int getChildrenCount(){
        return children.size();
    }

    public List<View> getChildren(){
        return new ArrayList<>(children);
    }

    public <T extends View> T getChildAt(int index){
        return (T) children.get(index);
    }

    public void addChild(View view){
        children.add(view);
        view.setParent(this);
        onChildAdded(view);
        view.requiresDraw = true;
        notifyTreeChanged();
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
    }
}
