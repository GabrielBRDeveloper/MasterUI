package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.*;

public class LinearLayout extends ViewGroup {
    public static int ORIENTATION_HORIZONTAL = 1;
    public static int ORIENTATION_VERTICAL = 0;

    private int orientation;
    private final Rect rect = new Rect();

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchRaw(AttrList.orientation, value -> {
            if ("horizontal".equals(value)){
                setOrientation(ORIENTATION_HORIZONTAL);
            } else {
                setOrientation(ORIENTATION_VERTICAL);
            }
        });
    }


    @Override
    public void addChild(View view) {
        LayoutParams params = new LayoutParams(0,0);
        params.from(view.getLayoutParams());
        view.setLayoutParams(params);
        super.addChild(view);
    }

    @Override
    protected Point getChildLocation(View view) {
        Point point = new Point(getPaddingLeft(), getPaddingTop());
        int mh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mw = getMeasuredWidth()  - getPaddingRight() - getPaddingLeft();
        int index = getChildIndex(view);
        Gravity.applyGravity(getGravity(),view.getMeasuredWidth(),view.getMeasuredHeight(),mw,mh,rect);

        for (int i = 0; i < index; i++){
            View child = getChildAt(i);
            if (orientation == ORIENTATION_HORIZONTAL){
                point.x += child.getMeasuredWidth();
            } else {
                point.y += child.getMeasuredHeight();
            }
        }

        LayoutParams params = (LayoutParams) view.getLayoutParams();

        point.x += params.getMarginLeft() - params.getMarginRight();
        point.y += params.getMarginTop() - params.getMarginBottom();

        if (orientation == ORIENTATION_HORIZONTAL) {
            point.y += rect.top;
        } else {
            point.x += rect.left;
        }


        return point;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestLayout();
    }

    private static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
