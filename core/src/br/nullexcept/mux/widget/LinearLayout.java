package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.graphics.Rect;
import br.nullexcept.mux.graphics.Size;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.*;

import java.util.List;

public class LinearLayout extends ViewGroup {
    public static int ORIENTATION_HORIZONTAL = 1;
    public static int ORIENTATION_VERTICAL = 0;

    private int orientation;
    private int dividerSize;

    private final Rect rect = new Rect();

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchDimension(AttrList.dividerSize, value -> dividerSize = value.intValue());
        attrs.searchRaw(AttrList.orientation, value -> {
            if ("horizontal".equals((value+"").toLowerCase())){
                setOrientation(ORIENTATION_HORIZONTAL);
            } else {
                setOrientation(ORIENTATION_VERTICAL);
            }
        });
    }

    @Override
    public void addChild(View view, ViewGroup.LayoutParams params) {
        if (params == null) {
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        LayoutParams newParams = new LayoutParams(0,0);
        newParams.from(params);
        view.setLayoutParams(newParams);

        super.addChild(view, newParams);
    }

    @Override
    protected Point getChildLocation(View view) {
        return orientation == ORIENTATION_VERTICAL ? getChildLocationVertical(view) : getChildLocationHorizontal(view);
    }

    private Size measureHorizontal() {
        Size size = new Size();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBILITY_GONE) continue;
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            size.width += child.getMeasuredWidth() + params.getMarginLeft() + params.getMarginRight();
            size.height = Math.max(child.getMeasuredHeight() + params.getMarginTop() + params.getMarginBottom(), size.height);
            if (i != getChildrenCount()) size.width += dividerSize;
        }
        return size;
    }

    private Size measureVertical() {
        Size size = new Size();
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBILITY_GONE) continue;
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            size.width  = Math.max(size.width, child.getMeasuredWidth() + params.getMarginLeft() + params.getMarginRight());
            size.height += child.getMeasuredHeight() + params.getMarginTop() + params.getMarginBottom();
            if (i < getChildrenCount()-1) {
                size.height += dividerSize;
            }
        }
        return size;
    }

    @Override
    protected Size onMeasureContent(int parentWidth, int parentHeight) {
        return orientation == ORIENTATION_VERTICAL ? measureVertical() : measureHorizontal();
    }

    private Point getChildLocationVertical(View view) {
        if (view.getVisibility() == VISIBILITY_GONE)
            return new Point(0,0);
        Size wrapSize = measureVertical();
        LayoutParams params = (LayoutParams) view.getLayoutParams();

        int w = Math.max(0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - params.getMarginRight());
        int h = Math.max(0, getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - params.getMarginBottom());

        int posX = Gravity.apply(Gravity.horizontal(getGravity()),w,wrapSize.width);
        int posY = Gravity.apply(Gravity.vertical(getGravity()),h, wrapSize.height);

        posX = Math.max(0, posX);
        posY = Math.max(0, posY);

        Point pos = new Point(0, posY);
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child.equals(view)) break;
            if (child.getVisibility() == VISIBILITY_GONE) continue;
            LayoutParams childParams = (LayoutParams) child.getLayoutParams();
            pos.y += childParams.getMarginTop() + childParams.getMarginBottom() + child.getMeasuredHeight();
        }

        pos.x = posX + Gravity.apply(Gravity.horizontal(getGravity()),wrapSize.width,view.getMeasuredWidth());
        pos.x += params.getMarginLeft();

        pos.y += getPaddingTop() + params.getMarginTop();
        pos.x += getPaddingLeft() + params.getMarginLeft();

        return pos;
    }

    private Point getChildLocationHorizontal(View view) {
        if (view.getVisibility() == VISIBILITY_GONE)
            return new Point(0,0);
        Size wrapSize = measureHorizontal();

        LayoutParams params = (LayoutParams) view.getLayoutParams();

        int w = Math.max(0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - params.getMarginRight());
        int h = Math.max(0, getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - params.getMarginBottom());

        int posX = Gravity.apply(Gravity.horizontal(getGravity()),w,wrapSize.width);
        int posY = Gravity.apply(Gravity.vertical(getGravity()),h, wrapSize.height);

        posX = Math.max(0, posX);
        posY = Math.max(0, posY);

        Point pos = new Point(posX, 0);
        for (int i = 0; i < getChildrenCount(); i++) {
            View child = getChildAt(i);
            if (child.equals(view)) break;
            if (child.getVisibility() == VISIBILITY_GONE) continue;
            LayoutParams childParams = (LayoutParams) child.getLayoutParams();
            pos.x += dividerSize;
            pos.x += childParams.getMarginLeft() + childParams.getMarginRight() + child.getMeasuredWidth();
        }

        pos.y = posY + Gravity.apply(Gravity.vertical(getGravity()),wrapSize.height,view.getMeasuredHeight());
        pos.y += params.getMarginTop() + getPaddingTop();
        pos.x += getPaddingLeft() + params.getMarginLeft();

        return pos;
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
