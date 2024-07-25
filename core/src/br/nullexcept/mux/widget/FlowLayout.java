package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.Gravity;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    private int dividerSize = 16;
    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeList init) {
        super(context, init);
        AttributeList attrs = initialAttributes();
        attrs.searchDimension(AttrList.dividerSize, x -> dividerSize = x.intValue());
    }

    @Override
    protected Point getChildLocation(View view) {
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight();
        int y = 0;
        int x = 0;
        boolean find = false;
        int lineCount = 0;
        int w = 0;
        int mh = 0;
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child.getMeasuredWidth() + w > width) {
                if (find) break;
                if (lineCount == 0) {
                    mh = Math.max(mh, child.getMeasuredHeight());
                    if (child.equals(view)) {
                        w = child.getMeasuredWidth();
                        break;
                    }
                    y += mh + dividerSize;
                } else {
                    y += mh + dividerSize;
                    i--;
                }

                x = 0;
                w = 0;
                mh = 0;
                lineCount = 0;
            } else {
                if (child.equals(view)) {
                    find = true;
                }
                w += child.getMeasuredWidth() + dividerSize;
                if (!find){
                    x += child.getMeasuredWidth() + dividerSize;
                }
                mh = Math.max(mh, child.getMeasuredHeight());
                lineCount++;
            }
        }

        int offsetX = Gravity.apply(Gravity.horizontal(getGravity()),width,lineCount == 0 ? w : w - dividerSize);
        int offsetY = Gravity.apply(Gravity.vertical(getGravity()),mh, view.getMeasuredHeight());

        x += getPaddingLeft() + offsetX;
        y += getPaddingTop() + offsetY;
        return new Point(x,y);
    }

    @Override
    public void addChild(View view, ViewGroup.LayoutParams params) {
        LayoutParams newParams = new LayoutParams(params.width,params.height);
        newParams.from(params);
        super.addChild(view, newParams);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
