package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewAttrs;
import br.nullexcept.mux.view.ViewGroup;

public class LinearLayout extends ViewGroup {
    public static int ORIENTATION_HORIZONTAL = 1;
    public static int ORIENTATION_VERTICAL = 0;

    private int orientation;
    private final Rect rect = new Rect();

    public LinearLayout(Context context) {
        super(context);
    }

    public LinearLayout(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    @Override
    protected void onInflate(AttributeList attr) {
        super.onInflate(attr);
        attr.searchRaw(ViewAttrs.orientation, value -> {
            if ("horizontal".equals(value)){
                setOrientation(ORIENTATION_HORIZONTAL);
            } else {
                setOrientation(ORIENTATION_VERTICAL);
            }
        });
    }

    @Override
    public void addChild(View view) {
        view.setLayoutParams(new LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height));
        super.addChild(view);
    }

    @Override
    protected Point getChildLocation(View view) {
        Point point = new Point(getPaddingLeft(), getPaddingTop());
        int index = getChildIndex(view);
        for (int i = 0; i < index; i++){
            View child = getChildAt(i);
            if (orientation == ORIENTATION_HORIZONTAL){
                point.x += child.getMeasuredWidth();
            } else {
                point.y += child.getMeasuredHeight();
            }
        }

        return point;
    }

    @Override
    public void measure() {
        super.measure();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestLayout();
    }

    private static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
