package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.MarginLayoutParams;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class FrameLayout extends ViewGroup {
    public FrameLayout(Context context) {
        super(context);
    }

    public FrameLayout(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    @Override
    protected Point getChildLocation(View view) {
        Point point = new Point();
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        int w = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
        int h = getMeasuredHeight()-getPaddingTop()-getPaddingBottom();

        int cw = view.getMeasuredWidth() + params.getMarginLeft() + params.getMarginRight();
        int ch = view.getMeasuredHeight() + params.getMarginTop() + params.getMarginTop();

        int x = Gravity.apply(Gravity.horizontal(params.gravity), w, cw);
        int y = Gravity.apply(Gravity.vertical(params.gravity), h, ch);

        y += params.getMarginTop();
        x += params.getMarginLeft();

        x += getPaddingLeft();
        y += getPaddingTop();

        point.set(x,y);

        return point;
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        Size size = new Size();
        for (View child: getChildren()){
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int x = Math.max(0, getChildLocation(child).x - getPaddingLeft());
            size.width = Math.max(x + child.getMeasuredWidth() + params.getMarginRight(), size.width);
            int y = Math.max(0, getChildLocation(child).y - getPaddingTop());
            size.height = Math.max(y+child.getMeasuredHeight() + params.getMarginBottom(), size.height);
        }
        return size;
    }

    @Override
    public void addChild(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        LayoutParams newParams = new LayoutParams(0,0);
        newParams.from(params);

        super.addChild(view, newParams);
    }

    public static class LayoutParams extends MarginLayoutParams {
        private int gravity = Gravity.LEFT;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        @Override
        public void from(ViewGroup.LayoutParams params) {
            if (params instanceof LayoutParams) {
                this.gravity = ((LayoutParams) params).gravity;
            }
            super.from(params);
        }
    }
}
