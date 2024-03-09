package br.nullexcept.mux.widget;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;
import br.nullexcept.mux.res.AttributeList;
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
        return new Point(params.x, params.y);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;

        public LayoutParams(int x, int y, int width, int height){
            super(width, height);
            this.x = x;
            this.y = y;
        }

        public LayoutParams(int width, int height) {
            this(0,0, width, height);
        }
    }
}
