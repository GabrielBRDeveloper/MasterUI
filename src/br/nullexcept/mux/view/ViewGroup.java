package br.nullexcept.mux.view;

import br.nullexcept.mux.app.Context;
import br.nullexcept.mux.graphics.Point;

import java.util.ArrayList;

public class ViewGroup extends View {
    private final ArrayList<View> children = new ArrayList<>();

    public ViewGroup(Context context) {
        super(context);
    }

    protected Point getChildLocation(View view) {
        return new Point(0,0);
    }

    protected void requestLayout(){

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
