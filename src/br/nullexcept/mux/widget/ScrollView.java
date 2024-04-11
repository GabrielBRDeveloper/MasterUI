package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.*;
import br.nullexcept.mux.graphics.drawable.ColorDrawable;
import br.nullexcept.mux.input.KeyEvent;
import br.nullexcept.mux.input.MotionEvent;
import br.nullexcept.mux.input.MouseEvent;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.AttrList;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class ScrollView extends ViewGroup {
    private final double[] scroll = new double[2];
    private final Point space = new Point();
    private final Point pixelScroll = new Point();
    private final ScrollContainer container;
    private Drawable scrollDrawable = new ColorDrawable(Color.GREEN);
    private final Rect rect = new Rect();
    private int scrollbarWeight = 10;
    private final Point mouseScroll = new Point();
    private boolean capturedMouseScroll = true;
    private final Rect scrollbar = new Rect();

    public ScrollView(Context context) {
        super(context);
    }

    public ScrollView(Context context, AttributeList attrs) {
        super(context, attrs);
        attrs = initialAttributes();
        attrs.searchDrawable(AttrList.scrollbar, this::setScrollbarDrawable);
    }

    {
        container = new ScrollContainer(getContext());
        container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        super.addChild(container);
    }


    public void setScrollbarDrawable(Drawable drawable) {
        scrollDrawable = drawable;
        measureContent();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        measureScroll();

        int ch = container.getMeasuredHeight();
        int vh = getMeasuredHeight();

        if (ch > vh){
            double rest = (double) vh / ch;
            float localHeight = getHeight() - getPaddingTop() - getPaddingBottom();

            int h = (int) Math.round(localHeight * rest);
            h = Math.max(10, h);
            int y = (int) Math.round((localHeight - h) * scroll[1]);
            scrollbar.set(getWidth()-scrollbarWeight, y+getPaddingTop(), getWidth(), getPaddingTop()+y+h);
            scrollDrawable.setBounds(scrollbar);
            scrollDrawable.draw(canvas);
        } else {
            scrollbar.set(0,0,0,0);
        }
    }

    @Override
    protected void onTreeChanged() {
        super.onTreeChanged();
        measureContent();
        capturedMouseScroll = true;
    }

    @Override
    protected boolean dispatchMouseEvent(MouseEvent mouseEvent) {

        if (mouseEvent.getAction() == MotionEvent.ACTION_DOWN && mouseEvent.getTarget() == -1) {
            if (scrollbar.inner(mouseEvent.getX(), mouseEvent.getY())) {
                mouseEvent.setTarget(hashCode());
                return true;
            }
        } else if (mouseEvent.getTarget() == hashCode()) {
            double percent = mouseEvent.getY() / getMeasuredHeight();
            percent = Math.max(0, Math.min(1.0, percent));
            scroll[1] = percent;
            measureContent();
            return true;
        }

        if (capturedMouseScroll){
            capturedMouseScroll = false;
            mouseScroll.set(mouseEvent.getScroll().x, mouseEvent.getScroll().y);
        } else {
            Point mouseScroll = mouseEvent.getScroll();
            if (mouseScroll.y != this.mouseScroll.y) {
                int diff = this.mouseScroll.y - mouseScroll.y;
                double factor = (1.0/space.y) * (diff * 20);
                scroll[1] += factor;
                measureContent();
            }
            this.mouseScroll.set(mouseScroll.x, mouseScroll.y);
        }
        return super.dispatchMouseEvent(mouseEvent);
    }

    @Override
    public void addChild(View view) {
        container.addChild(view);
    }

    @Override
    public void removeChild(View view) {
        container.removeChild(view);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    protected void onChildAdded(View view) {
        super.onChildAdded(view);
        measureContent();
    }

    private void measureScroll() {
        scroll[0] = Math.max(0.0, Math.min(1.0, scroll[0]));
        scroll[1] = Math.max(0.0, Math.min(1.0, scroll[1]));

        View child = container.getChildAt(0);

        int ch = child.getMeasuredHeight();
        int cw = child.getMeasuredWidth();

        int mh = getMeasuredHeight() - ch;
        int mw = getMeasuredWidth() - cw;

        mh = Math.min(mh,0);
        mw = Math.min(mw,0);

        int sx = (int) Math.round(mw * scroll[0]);
        int sy = (int) Math.round(mh * scroll[1]);

        sx = Math.min(0, sx);
        sy = Math.min(0, sy);

        space.set(Math.abs(mw), Math.abs(mh));

        pixelScroll.set(sx, sy);
    }

    private void measureContent() {
        if (container.getChildrenCount() == 0) {
            return;
        }

        measure();
        measureScroll();
        container.invalidate();
        invalidate();
    }

    @Override
    protected void onKeyEvent(KeyEvent keyEvent) {
        super.onKeyEvent(keyEvent);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        measureContent();
    }

    private class ScrollContainer extends ViewGroup {
        public ScrollContainer(Context context) {
            super(context);
        }

        public ScrollContainer(Context context, AttributeList attrs) {
            super(context, attrs);
        }

        {
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        @Override
        public void measure() {
            super.measure();
        }

        @Override
        protected void onMeasure(int width, int height) {
            super.onMeasure(width, height);
            measureContent();
        }

        @Override
        protected Point getChildLocation(View view) {
            return pixelScroll;
        }

        @Override
        protected void onChildAdded(View view) {
            super.onChildAdded(view);
            measureContent();
        }
    }
}
