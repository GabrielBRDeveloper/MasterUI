package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;
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
    public void addChild(View view) {
        view.setLayoutParams(new LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height));
        super.addChild(view);
    }

    @Override
    protected Point getChildLocation(View view) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        return new Point(params.position.x + getPaddingLeft(), params.position.y + getPaddingTop());
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        int space = 0;
        for (int i = 0; i < getChildrenCount(); i++){
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();

            Gravity.applyGravity(getGravity(),w,h,width,height,rect);
            if (orientation == ORIENTATION_HORIZONTAL){
                params.position.set(space, rect.top);
                space += w;
            } else {
                params.position.set(rect.left, space);
                space += h;
            }
            child.measureBounds();
        }
        measure();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        requestLayout();
    }


    private static class LayoutParams extends ViewGroup.LayoutParams {
        private final Point position = new Point();
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
