package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.res.AttributeList;
import br.nullexcept.mux.view.MarginLayoutParams;
import br.nullexcept.mux.view.View;
import br.nullexcept.mux.view.ViewGroup;

public class AbsoluteLayout extends ViewGroup {

    public AbsoluteLayout(Context context) {
        super(context);
    }

    public AbsoluteLayout(Context context, AttributeList attrs) {
        super(context, attrs);
    }

    @Override
    public void addChild(View view) {
        if (!(view.getLayoutParams() instanceof LayoutParams)){
            view.setLayoutParams(new LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height));
        }
        super.addChild(view);
    }

    @Override
    protected Point getChildLocation(View view) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        return new Point(params.x + getPaddingLeft(), params.y + getPaddingTop());
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;

        public LayoutParams(int x, int y, int width, int height){
            super(width, height);
            this.x = x;
            this.y = y;
        }

        @Override
        public void from(ViewGroup.LayoutParams params) {
            if (params instanceof MarginLayoutParams) {
                x = ((MarginLayoutParams) params).getMarginLeft();
                y = ((MarginLayoutParams) params).getMarginTop();
            }
            super.from(params);
        }

        public LayoutParams(int width, int height) {
            this(0,0, width, height);
        }
    }
}
